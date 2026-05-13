package com.flj.fljimagesearchmcp.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ImageSearchToolTest {

    @Resource
    private ImageSearchTool imageSearchTool;

    @Test
    void search() {
        String query = "nature"; // 搜索关键词
        String result = imageSearchTool.search(query);
        assertNotNull(result);
    }
}