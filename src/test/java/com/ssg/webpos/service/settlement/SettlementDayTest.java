package com.ssg.webpos.service.settlement;

import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.service.SettlementDayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SettlementDayTest {
    @Autowired
    SettlementDayService settlementDayService;

    @Test
    void contextVoid() {
        String date = "2023-05";
        List<SettlementDay> lists = settlementDayService.selectByYearMonth(date);
        for (SettlementDay list:lists) {
            System.out.println(list);
        }
    }
}
