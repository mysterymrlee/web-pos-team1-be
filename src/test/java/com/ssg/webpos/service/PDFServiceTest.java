//package com.ssg.webpos.service;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.util.Assert;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;
//
//import java.io.ByteArrayOutputStream;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//public class PDFServiceTest {
//
//    @Autowired
//    private PDFService pdfService;
//
//    @MockBean
//    private TemplateEngine templateEngine;
//
//    @Test
//    public void testCreatePDF() throws Exception {
//        // 테스트를 위해 필요한 모든 설정을 하고 테스트용 데이터를 적절하게 설정합니다.
//
//        // 템플릿 엔진이 반환할 HTML 내용을 지정합니다.
//        String expectedHtmlContent = "<html><body><h1>Test</h1></body></html>";
//        // 템플릿 엔진이 반환할 HTML 내용에 대한 Mock 설정을 합니다.
//        when(templateEngine.process(eq("test"), any(Context.class))).thenReturn(expectedHtmlContent);
//
//        // PDF 생성
//        byte[] pdfBytes = pdfService.createPDF();
//
//        // 생성된 PDF 검증
//        Assert.notNull(pdfBytes, "PDF 생성 실패");
//        Assert.isTrue(pdfBytes.length > 0, "PDF 내용이 비어 있음");
//        // PDF 내용을 분석하고 원하는 결과가 포함되어 있는지 테스트합니다.
//        // 여기서는 간단하게 PDF의 길이만 확인합니다.
//        // 실제 테스트 시나리오에 맞게 추가 검증을 수행하면 됩니다.
//        // 예를 들어, PDF의 내용을 파싱하여 특정 요소가 있는지 확인할 수도 있습니다.
//    }
//}
//
