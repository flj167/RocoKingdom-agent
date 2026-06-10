package com.flj.fljaiagent.util;

import java.util.regex.Pattern;

/**
 * Markdown → HTML 简易转换，支持粗体、斜体、标题、有序/无序列表
 */
public class MarkdownUtil {

    public static String toHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) return markdown;
        String html = markdown;

        // 标题 (# → <h1>, ## → <h2>, ### → <h3>)
        html = html.replaceAll("(?m)^### (.+)", "<h3>$1</h3>");
        html = html.replaceAll("(?m)^## (.+)", "<h2>$1</h2>");
        html = html.replaceAll("(?m)^# (.+)", "<h1>$1</h1>");

        // 粗体 **text**
        html = html.replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>");

        // 斜体 *text*
        html = html.replaceAll("(?!<[^>]*)\\*(?!\\s)(.+?)(?<!\\s)\\*(?![^<]*>)", "<em>$1</em>");

        // 无序列表 (- item 或 * item)
        html = html.replaceAll("(?m)^[*-] (.+)", "<li>$1</li>");

        // 换行 → <br>
        html = html.replace("\n\n", "<br><br>");

        return html;
    }
}
