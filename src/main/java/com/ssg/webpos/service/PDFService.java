package com.ssg.webpos.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
public class PDFService {

    private final TemplateEngine templateEngine;

    @Autowired
    public PDFService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] createPDF() throws Exception {
        // Thymeleaf 템플릿 엔진을 사용하여 HTML을 렌더링
        Context context = new Context();
        String htmlContent = templateEngine.process("test", context);

        // HTML을 PDF로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        Document document = (Document) renderer.getDocument();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(writer.getOs());
        document.close();

        return outputStream.toByteArray();
    }
}

