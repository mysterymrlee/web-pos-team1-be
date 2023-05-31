package com.ssg.webpos.repository;

import com.ssg.webpos.domain.StockReport;
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
}
