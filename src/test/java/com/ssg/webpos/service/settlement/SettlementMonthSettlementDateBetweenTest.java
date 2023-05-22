package com.ssg.webpos.service.settlement;

import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import com.ssg.webpos.service.SettlementMonthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SettlementMonthSettlementDateBetweenTest {
    @Autowired
    SettlementMonthService settlementMonthService;
    @Test
    void contextVoid() {
        String year = "2023";
        List<SettlementMonth> lists = settlementMonthService.selectByYear(year);
        for(SettlementMonth list:lists) {
            System.out.println(list);
        }
    }
}
