package com.flj.fljaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyManusTest {
    @Resource
    private MyManus myManus;

    @Test
    void run() {
        String userPrompt = "请协助整理洛克王国手游「风眠省」区域的草系精灵完整攻略。具体任务顺序如下：\"\n" +
                "    + \"1. 使用搜索引擎查询“洛克王国手游 风眠省 草系精灵 刷新点”，将前5条结果的标题与链接写入文件 fengmian_province_search.txt；\"\n" +
                "    + \"2. 从上述搜索结果中提炼可靠的草系精灵名单、出现位置与捕捉条件，整理为结构化文本，保存为 fengmian_province_guide.txt；\"\n" +
                "    + \"3. 查找并抓取官方Wiki中风眠省背景故事页面（若不确定URL，可通过搜索“洛克王国 风眠省 背景”获取），提取其核心文本内容，生成PDF文档 fengmian_province.pdf；\"\n" +
                "    + \"4. 搜索一张风眠省场景的高清壁纸，下载并保存为 fengmian_province_wallpaper.jpg；\"\n" +
                "    + \"5. 使用终端命令在D盘根目录创建文件夹 RockStrategy，然后将步骤2、3、4中生成的三个文件移动至该文件夹内；\"\n" +
                "    + \"6. 全部任务完成后结束本次会话。";

        String result = myManus.run(userPrompt);
        assertNotNull(result);
    }
}