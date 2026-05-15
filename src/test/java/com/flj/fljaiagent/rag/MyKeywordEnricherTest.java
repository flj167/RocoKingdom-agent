package com.flj.fljaiagent.rag;

import com.flj.fljaiagent.app.RocoKindomApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class MyKeywordEnricherTest {

    @Resource
    private RocoKindomApp rocoKindomApp;

    @Test
    void rocoAppVectorStore() {
        String chatId = UUID.randomUUID().toString();
        String message = "新手有什么容易抓，还强势的PVP精灵推荐";
        String answer =  rocoKindomApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }
}