package com.flj.fljaiagent.tool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * 用jsop进行网页查询
 */
public class JoupWebScrapingTool {

    //爬取url网页HTML信息
    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "The url of the web page to be crawled") String url){
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.html();
        } catch (IOException e) {
            return "Error scraping web page:"+e.getMessage();
        }
    }
}
