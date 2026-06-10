package com.flj.fljaiagent.tool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 用jsop进行网页爬取
 */
public class JoupWebScrapingTool {

    //爬取url网页内容，提取文本、图片、链接
    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "The url of the web page to be crawled") String url){
        try {
            Document doc = Jsoup.connect(url).get();
            StringBuilder sb = new StringBuilder();
            sb.append("标题：").append(doc.title()).append("\n\n");
            sb.append("正文：\n").append(doc.body().text()).append("\n\n");
            sb.append("图片链接：\n");
            doc.select("img[src]").forEach(img -> {
                String src = img.absUrl("src");
                if (!src.isEmpty()) {
                    sb.append(src).append("\n");
                }
            });
            return sb.toString();
        } catch (IOException e) {
            return "Error scraping web page:"+e.getMessage();
        }
    }
}
