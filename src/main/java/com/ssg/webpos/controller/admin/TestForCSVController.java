package com.ssg.webpos.controller.admin;

import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.dto.hqSale.HqListForSaleDTO;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.service.hqController.csv.CsvService;
import com.ssg.webpos.service.hqController.method.SaleMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.csv.CSVFormat; // 1.9에서 1.9.0으로 변경하니 활성화되었다.
import org.apache.commons.csv.CSVPrinter;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hq1")
@Slf4j
@RequiredArgsConstructor
public class TestForCSVController {
    private final SettlementDayRepository settlementDayRepository;
    private final SaleMethodService saleMethodService;
    private final CsvService csvService;


    @GetMapping("/test")
    public ResponseEntity<InputStreamResource> downlaod() throws IOException {

        List<SettlementDay> settlementDayList = settlementDayRepository.listFor1WeekASC();
        List<HqListForSaleDTO> list = saleMethodService.makeHqListForSaleDTO(settlementDayList); // csv 에 넣을 DTO 생성
        String filePath = "http://3.36.176.254:8080/api/v1/hq1/test";
        try (BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write('\ufeff');
            // CSV 헤더 작성
            writer.append("정산일자, 가게명, 수수료, 정산금액, 원가, 이익");
            writer.append("\n");

            // CSV 데이터 작성
            for (HqListForSaleDTO HqListForSaleDTO : list) {
                // 무조건 String 타입으로 만들어서 넣어야한다.
                String settlementDate = String.valueOf(HqListForSaleDTO.getSettlementDate());
                writer.append(settlementDate);
                writer.append(",");
                writer.append(HqListForSaleDTO.getStoreName());
                writer.append(",");
                writer.append(String.valueOf(HqListForSaleDTO.getCharge()));
                writer.append(",");
                writer.append(String.valueOf(HqListForSaleDTO.getSettlementPrice()));
                writer.append(",");
                writer.append(String.valueOf(HqListForSaleDTO.getOriginPrice()));
                writer.append(",");
                writer.append(String.valueOf(HqListForSaleDTO.getProfit()));
                writer.append("\n");
            }



            writer.flush();
        File file = new File("http://3.36.176.254:8080/api/v1/hq/sale-management/list-csv/date=1week/storeId=0/startDate=0/endDate=0/download");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename=" + file.getName());

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/cotet-stream"))
                .body(resource);
    }
}

    // test2
    public void download2(HttpServletResponse response) throws IOException {
        List<SettlementDay> settlementDayList = settlementDayRepository.listFor1WeekASC();
        List<HqListForSaleDTO> list = saleMethodService.makeHqListForSaleDTO(settlementDayList); // csv에 넣을 DTO 생성

        // CSV 데이터 작성
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("정산일자, 가게명, 수수료, 정산금액, 원가, 이익\n");
        for (HqListForSaleDTO hqListForSaleDTO : list) {
            csvContent.append(hqListForSaleDTO.getSettlementDate()).append(",");
            csvContent.append(hqListForSaleDTO.getStoreName()).append(",");
            csvContent.append(hqListForSaleDTO.getCharge()).append(",");
            csvContent.append(hqListForSaleDTO.getSettlementPrice()).append(",");
            csvContent.append(hqListForSaleDTO.getOriginPrice()).append(",");
            csvContent.append(hqListForSaleDTO.getProfit()).append("\n");
        }

        // 파일 다운로드 설정
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=file.csv");

        // 파일 쓰기
        try (OutputStream outputStream = response.getOutputStream()) {
            outputStream.write(csvContent.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }

    @GetMapping("/test3")
    public ResponseEntity<InputStreamResource> download3() throws IOException {
        List<SettlementDay> settlementDayList = settlementDayRepository.listFor1WeekASC();
        List<HqListForSaleDTO> list = saleMethodService.makeHqListForSaleDTO(settlementDayList); // csv 에 넣을 DTO 생성

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), CSVFormat.DEFAULT)) {
            // CSV 헤더 작성
            csvPrinter.printRecord("정산일자", "가게명", "수수료", "정산금액", "원가", "이익");
            // CSV 데이터 작성
            for (HqListForSaleDTO hqListForSaleDTO : list) {
                csvPrinter.printRecord(
                        hqListForSaleDTO.getSettlementDate(),
                        hqListForSaleDTO.getStoreName(),
                        hqListForSaleDTO.getCharge(),
                        hqListForSaleDTO.getSettlementPrice(),
                        hqListForSaleDTO.getOriginPrice(),
                        hqListForSaleDTO.getProfit()
                );
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=file.csv");

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(outputStream.size())
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }

}
