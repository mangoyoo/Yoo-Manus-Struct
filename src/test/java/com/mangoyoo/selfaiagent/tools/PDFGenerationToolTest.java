package com.mangoyoo.selfaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "mangoyoo.pdf";
        String content = "i am mangoyoo";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}
