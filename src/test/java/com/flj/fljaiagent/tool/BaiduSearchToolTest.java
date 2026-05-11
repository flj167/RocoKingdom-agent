package com.flj.fljaiagent.tool;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BaiduSearchToolTest {

    @Resource
    private BaiduSearchTool baiduSearchTool;

    @Test
    void search() {
        BaiduSearchTool.SearchResult result = baiduSearchTool.search("范良建", 1);
        //输出网页列表
        List<BaiduSearchTool.OrganicResult> results = result.getResults();
        results.forEach(r -> {
            System.out.println("位置: " + r.getPosition());
            System.out.println("标题: " + r.getTitle());
            System.out.println("链接: " + r.getLink());
            System.out.println("摘要: " + r.getSnippet());
            System.out.println("-----");
        });
        System.out.println("成功: " + result.isSuccess());
        System.out.println("消息: " + result.getMessage());
        System.out.println("结果数: " + result.getResults().size());
    }
}