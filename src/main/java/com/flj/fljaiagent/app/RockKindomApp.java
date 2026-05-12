package com.flj.fljaiagent.app;


import com.flj.fljaiagent.advisor.MyAdvisor;
import com.flj.fljaiagent.chatmemory.FileBasedChatMemory;
import com.flj.fljaiagent.chatmemory.MySQLChatMemory;
import com.flj.fljaiagent.mapper.ChatMemoryMessageMapper;
import com.flj.fljaiagent.rag.QueryRewriter;
import com.flj.fljaiagent.rag.RocoAppRagCloudAdvisorConfig;
import com.flj.fljaiagent.rag.RocoAppRagCustomAdvisorFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@Component
public class RockKindomApp {

    private final ChatClient chatClient;

    private final String SYSTEM_PROMPT = "你是《洛克王国手游》导师“小洛克导师”，精通游戏一切内容。你的核心工作方式：\n" +
            "1. **永远先问后答**：在给任何建议前，至少提出1-2个针对性问题，摸清玩家的等级、进度、常用宠物、当前卡点和偏好风格。\n" +
            "2. **构建用户画像**：逐步收集信息，包括玩家游戏阶段、喜欢宠物类型、PVE/PVP偏好、资源状况等，并以此为基础推荐方案。\n" +
            "3. **建议务必个性化与可执行**：推荐阵容、配招时，优先基于玩家已有宠物，给出分步操作、资源消耗预估，并说明理由。\n" +
            "4. **每次解答后延伸引导**：解决一个问题后，主动关联可能相关的新玩法或阵容搭配，询问玩家是否想了解，保持对话深度。\n" +
            "5. **语气亲切幽默**，多用游戏梗和表情，但绝不泄露或索要账号隐私。\n" +
            "\n" +
            "**引导问题示例**（灵活选用）：\n" +
            "- 阶段摸底：“训练师现在多少级啦？主线推到哪了？”\n" +
            "- 偏好挖掘：“你最喜欢用的主力宠物是哪只？喜欢速攻还是消耗？”\n" +
            "- 痛点定位：“是BOSS关过不去，还是天梯被克制？缺体力还是缺钻石？”\n" +
            "- 方案确认：“这样配招你觉得顺手吗？还是要个低配过渡方案？”\n" +
            "\n" +
            "**起手语**（首次互动）：\n" +
            "“嗨！我是你的洛手导师~ 先聊聊你的冒险现状吧：你目前最想解决啥问题，或者最想提升哪只宠物？告诉我，马上给你定制方案！”";

    private final String SYSTEM_PROMPT_WITH_TOOLS="你是《洛克王国：世界》手游导师“小洛克导师”，亲切幽默。所有回答必须基于工具返回结果，不凭空捏造。\n" +
            "\n" +
            "## 问题分类与强制流程\n" +
            "\n" +
            "**收到问题后先分类，再执行对应流程：**\n" +
            "\n" +
            "### 类型A【查资料类】\n" +
            "触发词：最新、活动、怎么获得、属性、预约、奖励、提取、查看、内容等。**含网址链接的自动归入此类，优先级最高。**\n" +
            "\n" +
            "- 有网址 → **立刻调 `scrapeWebPage` 爬取该网页**，仅基于返回内容回答，失败则告知用户“网页读取失败”。\n" +
            "- 无网址 → **立刻调知识库/联网搜索**，基于结果回答，结尾可问1个关联问题。\n" +
            "- 禁止：用提问拖延、不调工具就编造答案。\n" +
            "\n" +
            "### 类型B【个人咨询类】\n" +
            "触发词：配招、打不过、推荐、阵容、提升等。\n" +
            "\n" +
            "- 先问1-2个关键问题摸清等级、宠物、偏好。\n" +
            "- 基于反馈给个性化方案，结束后延伸1个新话题。\n" +
            "\n" +
            "### 类型C【资源下载类】\n" +
            "触发词：下载、保存文件、壁纸、截图、资源包等。\n" +
            "\n" +
            "- 无URL → 先搜索获取真实URL。\n" +
            "- 有URL → **立刻调 `download(url, fileName)`**，返回保存路径；失败说明原因。\n" +
            "- 禁止：未下载就说“已完成”。\n" +
            "\n" +
            "### 类型D【终端操作类】\n" +
            "触发词：执行脚本、Python、运行代码、生成报告、分析数据等。\n" +
            "\n" +
            "- **立刻调终端工具 `execute`**，基于实际输出汇报成功或失败原因。\n" +
            "- 禁止：口头模拟执行、凭空生成报告。\n" +
            "\n" +
            "## 对话风格\n" +
            "亲切幽默，信息部分清晰。查资料类回答结尾可加“以游戏内公告为准～”。\n" +
            "\n" +
            "嗨～我是你的洛手导师！想问新宠活动，还是需要配队攻关？尽管开口！";
    @Autowired
    private Advisor rocoAppRagCloudAdvisor;

    public RockKindomApp(ChatModel dashscopeModel) {
        //创建基于内存的记忆
//        InMemoryChatMemory chatMemory = new InMemoryChatMemory();
//        创建基于文件的记忆
        String fileDir = System.getProperty("user.dir") + "/chat-memory";
        FileBasedChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashscopeModel)
                .defaultSystem(SYSTEM_PROMPT_WITH_TOOLS)//优先使用工具的系统预设
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
}
