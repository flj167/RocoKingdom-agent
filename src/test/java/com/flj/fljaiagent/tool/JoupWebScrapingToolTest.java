package com.flj.fljaiagent.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoupWebScrapingToolTest {

    @Test
    void scrapeWebPage() {
        JoupWebScrapingTool tool = new JoupWebScrapingTool();
        String url = "https://www.baidu.com";
        String result = tool.scrapeWebPage(url);
        assertNotNull(result);
    }
}