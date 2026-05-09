package com.flj.fljaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 关键词元信息增强器
 */
@Component
public class MyKeywordEnricher {
    @Resource
    private ChatModel dashscopeChatModel;

    //添加关键词元信息
    List<Document> enrichDocumentsByKeyword(List<Document> documents){
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(dashscopeChatModel,5);//生成5个
        return enricher.apply(documents);
    }

    //加载文档并且把关键词元信息存入向量存储
    @Bean
    public VectorStore RocoAppVectorStore(EmbeddingModel dashscopeEmbeddingModel, RocoAppDocumentLoader rocoAppDocumentLoader){
        //创建向量存储
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        //加载文档
        List<Document> documents = rocoAppDocumentLoader.loadMarkdowns();
        //自动添加关键词元信息
        List<Document> enrichedDocumentsByKeyword = enrichDocumentsByKeyword(documents);
        //保存到向量库
        simpleVectorStore.add(enrichedDocumentsByKeyword);
        return simpleVectorStore;
    }

}
