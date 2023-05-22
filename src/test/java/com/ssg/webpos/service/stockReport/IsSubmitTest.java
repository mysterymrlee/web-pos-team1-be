package com.ssg.webpos.service.stockReport;

import com.ssg.webpos.domain.StockReport;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.service.StockReportService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class IsSubmitTest {
    @Autowired
    StockReportService stockReportService;
    @Test
    @Transactional
    void contextVoid() {
        boolean test = true;
        List<StockReport> list = stockReportService.selectByIsSubmit(test);
        for (StockReport item: list) {
            Hibernate.initialize(item.getProduct());
            Hibernate.initialize(item.getStore());
            System.out.println(item);
        }
    }
}
