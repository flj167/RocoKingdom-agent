package com.flj.fljaiagent.controller;

import com.flj.fljaiagent.agent.MyManus;
import com.flj.fljaiagent.app.RocoKindomApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private RocoKindomApp rocoKindomApp;
    @Resource
    private ChatModel dashscopeChatModel;
    @Resource
    private ToolCallback[] tools;

    /**
     * 同步输出AI对话应用
     * @param message
     * @param chatId
     * @return
     * 可以换用使用Tools或者知识库的聊天方法
     */
    @GetMapping("rocokindom_app/chat/sync")
    public String doChatWithRocoKindomAppBySync(String message,String chatId) {
        return rocoKindomApp.doChat(message, chatId);
    }

    /**
     * sse流式输出AI对话应用
     * @param message
     * @param chatId
     * @return
     * 可以换用使用Tools或者知识库的聊天方法
     */
    @GetMapping(value="rocokindom_app/chat/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithRocoKindomAppSSE(String message,String chatId) {
        return rocoKindomApp.doChatByStream(message, chatId);
    }

    /**
     * 使用SseEmitter实现sse流式输出AI对话应用
     * @param message
     * @param chatId
     * @return
     * 可以换用使用Tools或者知识库的聊天方法
     */
    @GetMapping(value="rocokindom_app/chat/sse/emitter")
    public SseEmitter doChatWithRocoKindomAppSseEmitter(String message, String chatId) {
        //创建一个事件很长的SseEmitter，确保AI对话有充足的时间
        SseEmitter sseEmitter = new SseEmitter(5 * 60 * 1000L);
        //获取Flux流并订阅
        rocoKindomApp.doChatByStream(message,chatId).subscribe(
                //处理每条消息
                chunk->{
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                },
                //处理错误
                sseEmitter::completeWithError,
                //处理结束
                sseEmitter::complete
        );
        return sseEmitter;
    }

    /**
     * 流式调用AI智能体
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManusAgent(String message){
        MyManus myManus = new MyManus(tools, dashscopeChatModel);
        return myManus.runStream(message);
    }
}
