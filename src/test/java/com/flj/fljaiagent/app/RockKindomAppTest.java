package com.flj.fljaiagent.app;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
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
            String message = "新手有什么容易抓，还强势的PVP精灵推荐";
            String answer =  rockKindomApp.doChatWithRag(message, chatId);
            Assertions.assertNotNull(answer);
    }
}