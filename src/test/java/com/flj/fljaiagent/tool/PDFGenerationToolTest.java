package com.flj.fljaiagent.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String content="我是大帅哥";
        String fileName="1.pdf";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}