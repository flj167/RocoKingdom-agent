package com.flj.fljaiagent.app;


import com.flj.fljaiagent.advisor.MyAdvisor;
import com.flj.fljaiagent.chatmemory.FileBasedChatMemory;
import com.flj.fljaiagent.rag.QueryRewriter;
import com.flj.fljaiagent.rag.RocoAppRagCustomAdvisorFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@Component
public class RocoKindomApp {

    private final ChatClient chatClient;

    private final String SYSTEM_PROMPT = "你是《洛克王国手游》导师“小洛克导师”，精通游戏一切内容。\n" +
            "【核心原则】先判断问题类型，再决定怎么答，绝不无脑反问。\n" +
            "\n" +
            "1. 事实类问题（问活动、宠物、数据、攻略等）：直接回答，给准确信息，可加一句相关小贴士，严禁先反问。\n" +
            "2. 个性化问题（配招、阵容、打不过等）：先问1-2个关键信息（等级、主力宠、偏好），再给方案，最后主动延伸一个新话题。\n" +
            "3. 收集画像：在多次对话中逐步了解玩家阶段、喜好、资源，让建议越来越准。\n" +
            "\n" +
            "语气亲切幽默，适当用梗和表情，不索要隐私。\n" +
            "\n" +
            "首次互动：“嗨～我是你的洛手导师！想问新宠活动，还是需要帮你配队？尽管开口！”";;
    @Autowired
    private Advisor rocoAppRagCloudAdvisor;

    public RocoKindomApp(ChatModel dashscopeModel) {
        //创建基于内存的记忆
//        InMemoryChatMemory chatMemory = new InMemoryChatMemory();
//        创建基于文件的记忆
        String fileDir = System.getProperty("user.dir") + "/chat-memory";
        FileBasedChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashscopeModel)
                .defaultSystem(SYSTEM_PROMPT)//优先使用工具的系统预设
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyAdvisor()
                )//内存记忆顾问
                .build();
    }

    //AI问答方法
    public String doChat(String message, String chatId) {
        String content = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))//设置顾问参数
                .call()
                .content();
        //打印AI输出结果
        log.info("AI回复: {}", content);
        return content;
    }

    //AI结构报告
    record RockReport(String tile, List<String> answers) {
    }

    //AI问答，还要输出报告(结构化输出)
    public RockReport doChatWithReport(String message, String chatId) {
        RockReport report = chatClient
                .prompt()
                .system("在回答用户问题后，请生成一个JSON格式的报告，包含：" +
                        "1. 'tile'字段：问题的主题（如'等级提升建议'）" +
                        "2. 'answers'数组：你给出的具体回答")//加上结构化输出格式要求
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))//设置顾问参数
                .call()
                .entity(RockReport.class);
        //打印AI输出结果
        log.info("Report:{}", report);
        return report;
    }

    @Resource
    private VectorStore rocoAppVectorStore;
    @Resource
    private QueryRewriter queryRewriter;
    //AI问答，启用RAG搜索增强生成功能
    public String doChatWithRag(String message, String chatId) {
        //使用查询重写器
        String rewritedMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(rewritedMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))//设置顾问参数
//                .advisors(new MyAdvisor())//开启日志输出
//                .advisors(new QuestionAnswerAdvisor(rocoAppVectorStore))//启用RAG搜索增强生成功能
//                .advisors(rocoAppRagCloudAdvisor) //启用云知识库RAG增强生成功能
                .advisors(RocoAppRagCustomAdvisorFactory.createRocoAppRagCustomAdvisor(rocoAppVectorStore,"异色"))//使用配置了自定义文档搜索器的拦截器,查询玩家目标是pvp的文档
                .call()
                .chatResponse();
        //打印AI输出结果
        String content=chatResponse.getResult().getOutput().getText();
        log.info("AI回复: {}", content);
        return content;
    }

    @Resource
    private ToolCallback[] allTools;
    //启用工具调用
    public String doChatWithTools(String message, String chatId) {
        //使用查询重写器
        String rewritedMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(rewritedMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))//设置顾问参数
//                .advisors(new MyAdvisor())//输入日志
                .tools(allTools)//使用所有工具
                .call()
                .chatResponse();
        //打印AI输出结果
        String content=chatResponse.getResult().getOutput().getText();
        log.info("AI回复: {}", content);
        return content;
    }

    //AI问答方法(流式输出)
    public Flux<String> doChatByStream(String message, String chatId) {
        //使用查询重写器
        String rewritedMessage = queryRewriter.doQueryRewrite(message);
         return  chatClient
                .prompt()
                .user(rewritedMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))//设置顾问参数
                 .advisors(new QuestionAnswerAdvisor(rocoAppVectorStore))//启用本地RAG搜索增强生成功能
                 .advisors(rocoAppRagCloudAdvisor)//启用云RAG搜索增强功能
                 .tools(allTools)//添加工具
                .stream()
                 .content();
    }
}
