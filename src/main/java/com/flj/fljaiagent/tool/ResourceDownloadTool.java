package com.flj.fljaiagent.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 资源下载工具调用(url)
 */
public class ResourceDownloadTool {

    //所有下载文件都保存在download文件夹下
    private final String FILE_DIR=FileConstant.FILE_SAVE_DIR+"/download";

    //从url下载资源,保存到对应名称的位置
    @Tool(description = "Download a resource from a given url")
    public String download(@ToolParam(description = "Download source url") String url,
                           @ToolParam(description = "Name of the file saved to") String fileName) {
        try {
            //先创建父目录
            FileUtil.mkdir(FILE_DIR);
            //具体保存路径
            String pathName=FILE_DIR+"/"+fileName;
            HttpUtil.downloadFile(url,new File(pathName));
            return "Successfully downloaded the resource from the url to："+pathName;
        } catch (Exception e) {
            return "Failed to download resource from url:"+e.getMessage();
        }
    }
}
