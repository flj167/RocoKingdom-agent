package com.flj.fljaiagent.tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 百度网页搜索工具 - 用于 AI Tool Calling
 */
@Slf4j
public class BaiduSearchTool {

    private final String apiKey;

    private static final String BAIDU_SEARCH_URL = "https://www.searchapi.io/api/v1/search";
    private final OkHttpClient httpClient ;
    private final ObjectMapper objectMapper ;

    public BaiduSearchTool(@Value("${search-api.api-key}") String apiKey) {
        // 启动时强制校验APIKey，为空直接抛出异常，避免运行时401
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("SearchAPI API Key 不能为空，请检查配置文件");
        }
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        log.info("BaiduSearchTool 初始化成功，APIKey: {}", apiKey.substring(0, 5) + "***");
    }

    //百度搜索
    @Tool(description = "Search the webpage on Baidu and return relevant search results")
    public SearchResult search(
            @ToolParam(description = "Search for keywords, support Chinese") String query,
            @ToolParam(description = "Page number, starting from 1, the default is 1") int pageNum) {
        try {
            String responseBody = executeSearch(query, pageNum);
            JsonNode root = objectMapper.readTree(responseBody);
            
            List<OrganicResult> results = new ArrayList<>();
            JsonNode organicResults = root.get("organic_results");
            
            if (organicResults != null && organicResults.isArray()) {
                for (JsonNode node : organicResults) {
                    OrganicResult result = new OrganicResult(
                            node.get("position").asInt(),
                            node.get("title").asText(),
                            node.get("link").asText(),
                            node.get("snippet").asText()
                    );
                    results.add(result);
                }
            }
            
            return new SearchResult(true, "Successful search", results);
        } catch (Exception e) {
            log.error("百度搜索失败: {}", e.getMessage());
            return new SearchResult(false, "Baidu search failed: " + e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * 执行百度搜索 API 请求
     */
    private String executeSearch(String query, int pageNum) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.get(BAIDU_SEARCH_URL).newBuilder();
        urlBuilder.addQueryParameter("engine", "baidu");
        urlBuilder.addQueryParameter("q", query);
        urlBuilder.addQueryParameter("page", String.valueOf(pageNum));
        urlBuilder.addQueryParameter("api_key", apiKey);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API 返回错误: " + response.code());
            }
            return response.body() != null ? response.body().string() : "{}";
        }
    }
    // ==================== 结果 DTO ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResult {
        private boolean success;
        private String message;
        private List<OrganicResult> results;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganicResult {
        private int position;      // 排名
        private String title;      // 标题
        private String link;       // 链接
        private String snippet;    // 摘要
    }
}