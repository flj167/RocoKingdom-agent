package com.flj.fljimagesearchmcp.tools;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片搜索工具类
 * 使用 Unsplash API 实现图片搜索功能
 */
public class ImageSearchTool {

    // Unsplash API 的基础地址
    private static final String UNSPLASH_API_URL = "https://api.unsplash.com";

    // Unsplash API key
    private String apiKey;

    public ImageSearchTool(@Value("${unsplash.access-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 搜索照片
     * Search for photos based on query parameters
     *
     * @param query 搜索关键词 - The search keyword to find photos
     * @return 返回搜索结果字符串
     */
    @Tool(description = "Search photos from Unsplash based on query")
    public String search(
            @ToolParam(description = "The search keyword to find photos") String query) {

        try {
            // 调用处理方法获取图片URL列表
            List<String> photoUrls = getPhotoUrls(query);

            // 判断搜索是否成功
            if (photoUrls != null && !photoUrls.isEmpty()) {
                System.out.println("Search successful! Found " + photoUrls.size() + " photos");
                return "Search successful";
            } else {
                System.out.println("Search failed! No photos found");
                return "Search failed";
            }
        } catch (Exception e) {
            System.out.println("Error occurred during search: " + e.getMessage());
            return "Search failed";
        }
    }

    /**
     * 获取图片URL列表（处理方法）
     * Process the search response and extract photo URLs
     *
     * @param query 搜索关键词
     * @return 返回图片URL列表
     */
    private List<String> getPhotoUrls(String query) {
        List<String> urlList = new ArrayList<>();

        try {
            // 构建请求参数
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("query", query);
            paramMap.put("page", 1);
            paramMap.put("per_page", 10);
            paramMap.put("client_id", apiKey);

            // 使用 hutool Http 工具发送请求
            String url = UNSPLASH_API_URL + "/search/photos";
            HttpResponse response = HttpUtil.createGet(url)
                    .form(paramMap)
                    .execute();

            // 检查响应状态码
            if (response.getStatus() == 200) {
                // 解析JSON响应
                String responseBody = response.body();
                JSONObject jsonObject = JSONUtil.parseObj(responseBody);

                // 获取results数组
                JSONArray results = jsonObject.getJSONArray("results");

                // 遍历结果，提取图片URL
                if (results != null) {
                    for (int i = 0; i < results.size(); i++) {
                        JSONObject photo = (JSONObject) results.get(i);
                        JSONObject urls = photo.getJSONObject("urls");
                        if (urls != null) {
                            String regularUrl = urls.getStr("regular");
                            urlList.add(regularUrl);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error processing search results: " + e.getMessage());
        }

        return urlList;
    }
}