package com.flj.fljaiagent.agent;

import com.flj.fljaiagent.exception.AgentException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

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
