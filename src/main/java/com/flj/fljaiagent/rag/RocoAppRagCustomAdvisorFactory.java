package com.flj.fljaiagent.rag;

import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 自定义RAG拦截器创建工厂
 * 返回设置了文档搜索器的拦截器顾问
 */
public class RocoAppRagCustomAdvisorFactory {
    //创建自定义RAG拦截器
    public static Advisor createRocoAppRagCustomAdvisor(VectorStore vectorStore, String target) {
        //创建搜索条件
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("target",target)//搜索玩家目标匹配的文档
                .build();
        //创建文档搜索器
        VectorStoreDocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)//要查询的向量库
                .similarityThreshold(0.7)//相似度阈值
                .topK(5)//返回文档数
                .filterExpression(expression)
                .build();
        //创建问答增强器（上下文为空的时候直接问AI）
        ContextualQueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder()
                .allowEmptyContext(true)
                .build();
        //返回顾问
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .queryAugmenter(queryAugmenter)//上下文为空，直接问AI
                .build();
    }
}
