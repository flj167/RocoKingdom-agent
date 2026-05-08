//package com.flj.fljaiagent.demo.invoke;
//
//
//import dev.langchain4j.community.model.dashscope.QwenChatModel;
//
//public class LangChainAiInvoke {
//    public static void main(String[] args) {
//        QwenChatModel qwenModel = QwenChatModel.builder()
//                .apiKey(TestApiKey.API_KEY)
//                .modelName("qwen-max")
//                .build();
//        String answer = qwenModel.chat("我是大帅哥");
//        System.out.println(answer);
//    }
//}