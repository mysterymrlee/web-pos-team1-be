package com.ssg.webpos.service.settlement;

import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.domain.Store;
import com.ssg.webpos.dto.settlement.SettlementMonthReportDTO;
import com.ssg.webpos.service.SettlementMonthService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SettlementMonthRangeTest {
    @Autowired
    SettlementMonthService settlementMonthService;

    @Test
    void contextVoid() {
      String StartDate = "2023-02";
      String EndDate = "2023-03";
      List<SettlementMonth> settlementMonths = settlementMonthService.selectByMonthRange(StartDate,EndDate);
    }
}
