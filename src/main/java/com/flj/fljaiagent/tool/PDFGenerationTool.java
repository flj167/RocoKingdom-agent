package com.flj.fljaiagent.tool;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * PDF文档转化器工具调用
 */
public class PDFGenerationTool {

    //所有下载文件都保存在pdf文件夹下
    private final String FILE_DIR=FileConstant.FILE_SAVE_DIR+"/pdf";

    //把文字内容生成PDF并保存到本地文件
    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(@ToolParam(description = "Save the file name of the generated PDF") String fileName,
                              @ToolParam(description = "Content to be included in the PDF") String content){
        String filePath=FILE_DIR+"/"+fileName;
        try {
            //创建父目录
            FileUtil.mkdir(FILE_DIR);
            try (PdfWriter pdfWriter = new PdfWriter(filePath);//写入器
                 PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                 Document document = new Document(pdfDocument);){
                //设置中文
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);
                Paragraph paragraph = new Paragraph(content);//创建段落
                document.add(paragraph);//
            }
            return "Successfully generated PDF to:"+filePath;
        } catch (Exception e) {
            return "Failed to generate PDF:"+e.getMessage();
        }

    }
}
