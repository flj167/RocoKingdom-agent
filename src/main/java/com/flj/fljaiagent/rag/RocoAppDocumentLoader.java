package com.flj.fljaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取知识库文档并转换成Document列表
 */
@Slf4j
@Component
public class RocoAppDocumentLoader {
    private final ResourcePatternResolver resourcePatternResolver;

    public RocoAppDocumentLoader(ResourcePatternResolver resourcePatternResolver){
            this.resourcePatternResolver = resourcePatternResolver;
        }

        //加载文件
        public List<Document> loadMarkdowns () {
            ArrayList<Document> documents = new ArrayList<>();
            try {
                //拿到文档
                Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
                for (Resource resource : resources) {
                    String filename = resource.getFilename();
                    //创建文件读取器配置
                    MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                            .withHorizontalRuleCreateDocument(true)
                            .withIncludeCodeBlock(false)//不包括代码块
                            .withIncludeBlockquote(false)//不包括块引号
                            .withAdditionalMetadata("filename", filename)//添加标签
                            .build();

                    MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                    documents.addAll(reader.get());
                }
            } catch (IOException e) {
                log.error("文件加载出错", e);
            }
            return documents;
        }
    }
