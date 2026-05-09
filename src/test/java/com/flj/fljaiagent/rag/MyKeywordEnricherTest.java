package com.flj.fljaiagent.rag;

import com.flj.fljaiagent.app.RockKindomApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyKeywordEnricherTest {

    @Resource
    private RockKindomApp rockKindomApp;

    @Test
    void rocoAppVectorStore() {
        String chatId = UUID.randomUUID().toString();
        String message = "新手有什么容易抓，还强势的PVP精灵推荐";
        String answer =  rockKindomApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }
}