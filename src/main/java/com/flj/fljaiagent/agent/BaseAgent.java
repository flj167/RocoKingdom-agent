package com.flj.fljaiagent.agent;

import com.flj.fljaiagent.exception.AgentException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 * 子类必须实现step方法。
 */
@Slf4j
@Data
public abstract class BaseAgent {

    //agent名称
    private String name;
    //提示词
    private String systemPrompt;//系统预设
    private String nextPrompt;//下一次的提示词
    //agent状态(默认空闲)
    private AgentState state=AgentState.IDLE;
    //控制执行变量
    private int currentStep=0;//当前轮数
    private int maxStep=10;//最多轮数
    //LLM
    private ChatClient chatClient;
    //ChatMemory
    private List<Message> messageList=new ArrayList<>();

    /**
     * 执行代理的主循环
     * @param userPrompt
     * @return
     */
    public String run(String userPrompt){
        //参数校验
        if(StringUtil.isBlank(userPrompt)){
            throw new AgentException("Can not run agent with the empty user prompt");
        }
        if(state!=AgentState.IDLE){
            throw new AgentException("Agent is not idle, can not run,agent state:"+state);
        }
        //开始运行，更新属性
        state=AgentState.RUNNING;
        messageList.add(new UserMessage(userPrompt));
        //结果列表
        List<String> results=new ArrayList<>();
        try {
            //循环
            int i=0;
            while(i<maxStep&&state!=AgentState.FINISHED){
                int stepNum=i+1;//从1开始记
                log.info("now Step {}/maxSteps {}",stepNum,maxStep);//循环进度
                String stepResult = step();
                //记录执行结果
                String result="Step "+stepNum+":"+stepResult;
                results.add(result);
                i++;
            }
            //结束后更新状态
            if(currentStep>=maxStep){
                state=AgentState.FINISHED;
                results.add("Terminated: Reached max steps:"+maxStep);
            }
            return String.join("\n",results);
        } catch (Exception e) {
            state=AgentState.ERROR;
            log.error("Error executing agent",e);
            return "执行错误"+e.getMessage();
        } finally {
            //清理资源
            cleanup();
        }
    }

    /**
     * 流式输出
     * @param userPrompt
     * @return
     */
    public SseEmitter runStream(String userPrompt){
        //创建SseEmitter
        SseEmitter sseEmitter = new SseEmitter(5 * 60 * 1000L);
        //使用线程异步处理
        CompletableFuture.runAsync(()->{
        try {
            //参数校验
            if(StringUtil.isBlank(userPrompt)){
                sseEmitter.send("错误：不能用空的提示词运行agent");
                sseEmitter.complete();
            }
            if(state!=AgentState.IDLE){
                sseEmitter.send("错误：Agent不是空闲状态，不能运行，当前状态:"+state);
                sseEmitter.complete();
            }
            //开始运行，更新属性
            state=AgentState.RUNNING;
            messageList.add(new UserMessage(userPrompt));
            //结果列表
            List<String> results=new ArrayList<>();
            try {
                //循环
                int i=0;
                while(i<maxStep&&state!=AgentState.FINISHED){
                    int stepNum=i+1;//从1开始记
                    log.info("now Step {}/maxSteps {}",stepNum,maxStep);//循环进度
                    String stepResult = step();
                    //记录执行结果
                    String result="Step "+stepNum+":"+stepResult;
                    results.add(result);
                    //每一步结束，发送一次结果
                    sseEmitter.send(result);
                    i++;
                }
                //结束后更新状态
                if(currentStep>=maxStep){
                    state=AgentState.FINISHED;
                    results.add("Terminated: Reached max steps:"+maxStep);
                }
                //正常结束
                sseEmitter.complete();
            } catch (Exception e) {
                state=AgentState.ERROR;
                log.error("Error executing agent",e);
                try {
                    //发送错误信息
                    sseEmitter.send("执行错误：", MediaType.valueOf(e.getMessage()));
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
            } finally {
                //清理资源
                cleanup();
            }
        } catch (IOException e) {
            sseEmitter.completeWithError(e);
        }

        //事件回调
        //超时
        sseEmitter.onTimeout(()->{
            //更新agent状态
            this.state=AgentState.ERROR;
            //清理资源
            this.cleanup();
            //打warn日志
            log.warn("SSE connection timeout");
        });
        //连接结束
        sseEmitter.onCompletion(()->{
            //更新agent状态
            this.state=AgentState.FINISHED;
            //清理资源
            this.cleanup();;
            //打info日志
            log.info("SSE connection completed");
        });
    });
        return sseEmitter;
    }

    /**
     * 执行单步操作，必须由子类实现
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup(){
        //子类可以重写这个方法，清理资源
    }
}
