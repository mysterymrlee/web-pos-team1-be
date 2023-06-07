package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.StockReport;
import com.ssg.webpos.dto.hqStock.StockReportResponseDTO;
import com.ssg.webpos.service.hqController.method.HqControllerStockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class StockReportRepositoryTest {
    @Autowired
    StockReportRepository stockReportRepository;
    @Autowired
    HqControllerStockService hqControllerStockService;

    // LazyInitializationException이 일어났다.
    @Test
    void contextVoid() {
        List<StockReport> lists = stockReportRepository.findByStoreId(1L);
        for (StockReport list : lists) {
            System.out.println(list);
        }
    }

    @Test
    @Transactional
    void contextVoid1() {
        LocalDateTime date = LocalDateTime.parse("2023-01-08T00:00:00");
        List<StockReport> lists = stockReportRepository.findByCreatedDate(date);
        for (StockReport list : lists) {
            System.out.println(list);
        }
    }

    // JPA 오름차순 내림차순 테스트
    @Test
    void findByProductSaleStateOrderByProductSalePriceAsc() {
        List<StockReport> lists = stockReportRepository.findByProductSaleStateOrderByProductSalePriceAsc((byte)0);
        for (StockReport list : lists) {
            System.out.println(list);
        }
    }

    @Test
    void all() {
        List<StockReport> stockReportList = stockReportRepository.findByProductSaleState((byte) 0);
        List<StockReportResponseDTO> stockReportResponseDTOList = hqControllerStockService.getStockReportResponseDTOList(stockReportList);
        for (StockReportResponseDTO stockReportResponseDTO : stockReportResponseDTOList) {
            System.out.println(stockReportResponseDTO);
        }
    }

}
