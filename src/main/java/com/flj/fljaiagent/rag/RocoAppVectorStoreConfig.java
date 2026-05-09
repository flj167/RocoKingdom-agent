package com.flj.fljaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 向量存储配置类
 * 能创建向量存储
 */
@Configuration
public class RocoAppVectorStoreConfig {

    @Resource
    private RocoAppDocumentLoader rocoAppDocumentLoader;
    //把文件存入向量存储
    @Bean
    VectorStore rocoAppVectorStore(EmbeddingModel dashscopeEmbeddingModel){
        //创建向量存储
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        //读取知识库文件
        List<Document> documents = rocoAppDocumentLoader.loadMarkdowns();
        //embedding模型转换成向量并存入数据库
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }
}
