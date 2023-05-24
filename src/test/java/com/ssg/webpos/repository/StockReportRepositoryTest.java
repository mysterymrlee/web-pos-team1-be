package com.ssg.webpos.repository;

import com.ssg.webpos.domain.StockReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class StockReportRepositoryTest {
    @Autowired
    StockReportRepository stockReportRepository;
    @Test
    void contextVoid() {
        List<StockReport> r = stockReportRepository.findByIsSubmitAndStoreId(true,1L);
        for (StockReport stockReport: r) {
            System.out.println(stockReport);
        }
    }
}
