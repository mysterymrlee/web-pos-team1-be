package com.ssg.webpos.repository;

import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class SettlementMonthReeositoryTest {
    @Autowired
    SettlementMonthRepository settlementMonthRepository;
    @Test
    void contextVoid() {
        LocalDate start = LocalDate.parse("2023-02-01");
        LocalDate end = LocalDate.parse("2023-03-01");
        List<SettlementMonth> lists = settlementMonthRepository.findBySettlementDateBetween(start,end);
        for(SettlementMonth list: lists) {
            System.out.println(list);
        }
    }
}
