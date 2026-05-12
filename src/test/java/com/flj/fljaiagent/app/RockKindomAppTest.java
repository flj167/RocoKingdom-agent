package com.flj.fljaiagent.app;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RockKindomAppTest {

    @Resource
    private RockKindomApp rockKindomApp;

    @Test
    void testChat() {
        //创建一个UUID作为chatId
        String chatId = UUID.randomUUID().toString();
        //第一轮对话
        String message = "我现在是新手小白，等级10级，想抓几只速度快的宠物开图，有什么建议吗？";
        String answer = rockKindomApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        //第二轮
        message = "请你在这里面选出火属性的精灵";
        answer = rockKindomApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        //第三轮
        message = "我这个人物等级可以提升吗，从我的等级升级到满级大概需要多久？";
        answer = rockKindomApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void testChatWithReport() {
        //创建一个UUID作为chatId
        String chatId = UUID.randomUUID().toString();
        //第一轮对话
        String message = "我现在是新手小白，等级10级，大约多久能升到满级？新手最强宠物是谁？";
        RockKindomApp.RockReport rockReport = rockKindomApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(rockReport);
    }

    @Test
    void doChatWithRag() {
            String chatId = UUID.randomUUID().toString();
//            String message = "新手有什么容易抓，还强势的PVP精灵推荐";
//            String message = "我最近刚开始玩洛克王国手游，有啥简单有强势的精灵推荐一下吗？";
        String message = "这个游戏满级是几级？从1级升到满级大概需要多久？";
            String answer =  rockKindomApp.doChatWithRag(message, chatId);
            Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试获取当前系统时间
        testMessage("现在几点了");
        // 测试联网搜索问题的答案
        testMessage("洛克王国手游中，最新上线的宠物是哪只？");

        // 测试网页抓取：
        testMessage("从https://news.17173.com/z/lkwgsj2024/content/11142025/090429583.shtml这个网址中提取洛克王国手游的预约奖励内容和领取方式。");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的洛克王国精灵'卷毛鸭'图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");
//
        // 测试文件操作：保存用户档案
        testMessage("保存我的问答结果为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘新手必抓精灵’PDF，包含角色最低抓取等级、抓取方法");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = rockKindomApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }
}