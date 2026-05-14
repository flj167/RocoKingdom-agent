package com.flj.fljaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class ToolCallAgent extends ReActAgent {

    //可用的工具列表
    private final ToolCallback[] availableTools;
    //选择使用的工具列表(从对话响应中获取)
    private ChatResponse toolCallChatResponse;
    //工具调用管理
    private final ToolCallingManager toolCallingManager;
    //对话选项（用于手动设置选择工具）
    private ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools=availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        //禁用 SpringAI内置的工具调用机制，自己维护对话上下文
        this.chatOptions= DashScopeChatOptions.builder()
                .withProxyToolCalls(true)//自己代理处理
                .build();
    }

    /**
     * 处理当前状态，并且决定动作
     * @return
     */
    @Override
    public boolean think() {
        //判断是否有下一步提示词,处理当前状态
        if(StrUtil.isNotBlank(getNextPrompt())){
            //保存提示词到对话上下文
            UserMessage userMessage = new UserMessage(getNextPrompt());
            getMessageList().add(userMessage);
        }
        List<Message> messageList=getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);//将对话上下文和选项封装成Prompt对象
        try {
            //调用AI,得到工具调用结果
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            //记录响应结果,把信息保存到对话上下文
            this.toolCallChatResponse=chatResponse;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            //解析调用结果，获取要使用的工具列表
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();//要使用的工具列表
            //打印工具信息
            log.info(getName()+"的思考："+result);
            log.info(getName()+"决定要调用"+toolCallList.size()+"个工具");
            String toolsInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s,工具参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolsInfo);
            //不调用工具时，需要把AI回复的助手消息保存到上下文
            if(toolCallList.isEmpty()){
                getMessageList().add(assistantMessage);
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {
            log.error(getName()+" 在思考过程中出错了"+e.getMessage());
            //出错时把错误信息保存到上下文
            getMessageList().add(new AssistantMessage("Error: "+e.getMessage()));
            return false;
        }
    }

    /**
     * 执行工具并且处理结果
     * @return
     */
    @Override
    public String act() {
        if(!toolCallChatResponse.hasToolCalls()){
            return "没有工具调用";
        }
        //调用工具(使用toolCallingManager)
        Prompt prompt = new Prompt(getMessageList(), chatOptions);//将对话上下文和选项封装成Prompt对象
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        //记录对话上下文
        setMessageList(toolExecutionResult.conversationHistory());
        //获取工具调用结果（从对话上下文中获取最后一条消息，工具调用结果是最后一条消息）
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        //打印工具调用结果信息
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + "完成了任务，结果是：" + response.responseData())
                .collect(Collectors.joining("\n"));
        //判断是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate"));
        //如果执行了终止方法
        if(terminateToolCalled){
            setState(AgentState.FINISHED);//已完成
        }
        log.info(results);
        return results;
    }
}
