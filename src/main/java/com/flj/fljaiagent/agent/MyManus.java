package com.flj.fljaiagent.agent;

import com.flj.fljaiagent.advisor.MyAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class MyManus extends ToolCallAgent {

   //设置基本信息,可用工具列表通过统一注册类Bean获取
    public MyManus(ToolCallback[] avilableTools, ChatModel dashscopeChatModel){
        super(avilableTools);
        this.setName("fljManus");
        String SYSTEM_PROMPT="You are fljManus,an all-capable Al assistant, aimed at solving any task presented by the user.\n" +
                "You have various tools at your disposal that you can call upon to efficiently complete complex requests.\n" +
                "When answering without using tools, output the final answer directly in a concise, user-friendly manner. Do not output analysis steps or internal reasoning.\n" +
                "Always respond in Chinese (Simplified Chinese).";
        String NEXT_STEP_PROMPT="Based on user needs, proactively select the most appropriate tool or combination of tools.\n" +
                "For complex tasks, you can break down the problem and use different tools step by step to solve it. \n" +
                "After using each tool, clearly explain the execution results and suggest the next steps.\n" +
                "If you want to stop the interaction at any point, use the `terminate` tool/function call.";
        this.setSystemPrompt(SYSTEM_PROMPT);
        this.setNextPrompt(NEXT_STEP_PROMPT);
        //设置最大步骤数
        this.setMaxStep(20);
        //对话客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
