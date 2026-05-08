//package com.flj.fljaiagent.demo.invoke;
//
//import cn.hutool.http.HttpRequest;
//import cn.hutool.http.ContentType;
//import cn.hutool.json.JSONUtil;
//
//import java.util.Map;
//import java.util.List;
//
/// **
// * Http调用火山云AI模型
// */
//public class HttpAiInvoke {
//    public static void main(String[] args) {
//        String url = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";
//        String apiKey = TestApiKey.API_KEY;
//
//        Map<String, Object> body = Map.of(
//            "model", "doubao-seed-2-0-code-preview-260215",
//            "messages", List.of(
//                Map.of(
//                    "role", "user",
//                    "content", List.of(
//                        Map.of(
//                            "type", "image_url",
//                            "image_url", Map.of("url", "https://ts1.tc.mm.bing.net/th/id/OIP-C.ni19RVXFRT16haSfR2gx2gHaKw")
//                        ),
//                        Map.of(
//                            "type", "text",
//                            "text", "这张图片里面是什么东西？"
//                        )
//                    )
//                )
//            )
//        );
//
//        String response = HttpRequest.post(url)
//            .header("Content-Type", "application/json")
//            .header("Authorization", "Bearer " + apiKey)
//            .body(JSONUtil.toJsonStr(body), ContentType.JSON.getValue())
//            .execute()
//            .body();
//
//        System.out.println(response);
//    }
//}