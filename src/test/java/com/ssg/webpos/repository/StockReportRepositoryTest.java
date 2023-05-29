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

    // LazyInitializationException이 일어났다.
    @Test
    void contextVoid() {
        List<StockReport> lists = stockReportRepository.findByStoreId(1L);
        for (StockReport list : lists) {
            System.out.println(list);
        }
    }
}
