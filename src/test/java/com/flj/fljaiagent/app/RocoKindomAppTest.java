package com.flj.fljaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class RocoKindomAppTest {

    @Resource
    private RocoKindomApp rocoKindomApp;

    @Test
    void testChat() {
        //创建一个UUID作为chatId
        String chatId = UUID.randomUUID().toString();
        //第一轮对话
        String message = "我现在是新手小白，等级10级，想抓几只速度快的宠物开图，有什么建议吗？";
        String answer = rocoKindomApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        //第二轮
        message = "请你在这里面选出火属性的精灵";
        answer = rocoKindomApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        //第三轮
        message = "我这个人物等级可以提升吗，从我的等级升级到满级大概需要多久？";
        answer = rocoKindomApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void testChatWithReport() {
        //创建一个UUID作为chatId
        String chatId = UUID.randomUUID().toString();
        //第一轮对话
        String message = "我现在是新手小白，等级10级，大约多久能升到满级？新手最强宠物是谁？";
        RocoKindomApp.RockReport rockReport = rocoKindomApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(rockReport);
    }

    @Test
    void doChatWithRag() {
            String chatId = UUID.randomUUID().toString();
//            String message = "新手有什么容易抓，还强势的PVP精灵推荐";
//            String message = "我最近刚开始玩洛克王国手游，有啥简单有强势的精灵推荐一下吗？";
        String message = "这个游戏满级是几级？从1级升到满级大概需要多久？";
            String answer =  rocoKindomApp.doChatWithRag(message, chatId);
            Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
//        testMessage("洛克王国手游最新版本中，火系精灵「烈火战神」的推荐技能搭配是什么？");
//        testMessage("搜索洛克王国手游中火系精灵「烈火战神」的技能表，给我前5条结果的标题和链接");
//
//        testMessage("将以下内容保存为文件 'top_ice_spirits.txt'：冰系精灵排行榜第一名：雪影娃娃，第二名：冰龙王，第三名：寒冰皇");
//        testMessage("读取文件 'top_ice_spirits.txt' 的内容并展示给我");
//
//        testMessage("抓取百度百科中「洛克王国」词条的网页内容（链接：https://baike.baidu.com/item/%E6%B4%9B%E5%85%8B%E7%8E%8B%E5%9B%BD），并告诉我页面标题");
//
//        testMessage("生成一份 PDF 攻略，文件名为 'newbie_guide.pdf'，内容是：洛克王国手游新手必做任务：1.完成主线第一章；2.捕捉第一只精灵；3.开启每日签到。");
//
//        testMessage("下载洛克王国手游中宠物'卷毛鸭'的壁纸");
//
//        testMessage("在我的电脑上创建一个名为 'RockKingdom' 的文件夹，路径放在 D 盘根目录，使用终端命令完成");
//
//        testMessage("不需要做任何事，直接结束任务");

        //综合测试
        testMessage("请协助整理洛克王国手游「风眠省」区域的草系精灵完整攻略。具体任务顺序如下：\"\n" +
                "    + \"1. 使用搜索引擎查询“洛克王国手游 风眠省 草系精灵 刷新点”，将前5条结果的标题与链接写入文件 fengmian_province_search.txt；\"\n" +
                "    + \"2. 从上述搜索结果中提炼可靠的草系精灵名单、出现位置与捕捉条件，整理为结构化文本，保存为 fengmian_province_guide.txt；\"\n" +
                "    + \"3. 查找并抓取官方Wiki中风眠省背景故事页面（若不确定URL，可通过搜索“洛克王国 风眠省 背景”获取），提取其核心文本内容，生成PDF文档 fengmian_province.pdf；\"\n" +
                "    + \"4. 搜索一张风眠省场景的高清壁纸，下载并保存为 fengmian_province_wallpaper.jpg；\"\n" +
                "    + \"5. 使用终端命令在D盘根目录创建文件夹 RockStrategy，然后将步骤2、3、4中生成的三个文件移动至该文件夹内；\"\n" +
                "    + \"6. 全部任务完成后结束本次会话。");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = rocoKindomApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }
}