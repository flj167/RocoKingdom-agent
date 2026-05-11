package com.flj.fljaiagent.tool;

import cn.hutool.core.io.FileUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;


/**
 * 文件操作类
 */
public class FileOperationTool {
    //保存在file文件夹下
    private final String FILE_DIR=FileConstant.FILE_SAVE_DIR+"/file";

    //读取文件
    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to be read") String fileName){
        //根据文件名读取文件
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file:"+e.getMessage();
        }
    }

    //写入文件
    @Tool(description = "Write content to a file")
    public String writeFile(
            @ToolParam(description = "The name of the file to be written to") String fileName,
            @ToolParam(description = "Contents to be written to the file") String content){
        //根据文件名和内容写入文件
        String filePath = FILE_DIR + "/" + fileName;
        //创建文件目录
        try {
            FileUtil.mkdir(FILE_DIR);//确保父路径存在
            FileUtil.writeUtf8String(content, filePath);
            return "File saved successfully to: " + filePath;
        } catch (Exception e) {
            return "Error writing to file:"+e.getMessage();
        }

    }
}
