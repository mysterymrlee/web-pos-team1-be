package com.ssg.webpos.service.settlement;

import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import com.ssg.webpos.service.SettlementDayService;
import com.ssg.webpos.service.SettlementMonthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class SettlementSelectServiceTest {

    @Autowired
    SettlementDayRepository settlementDayRepository;
    @Autowired
    SettlementMonthRepository settlementMonthRepository;
    @Autowired
    SettlementDayService settlementDayService;
    @Autowired
    SettlementMonthService settlementMonthService;

    @Test
    void selectSettlementDayByStoreIdTest() throws IllegalArgumentException {
        Long storeId = 1L;
        List<SettlementDay> settlementDayList = settlementDayService.selectByStoreId(storeId);
        System.out.println(settlementDayList);
        Assertions.assertEquals(1, settlementDayList.size());
    }

    @Test
    void selectSettlementDayByDayTest() {
        String localdate = "51515";
        List<SettlementDay> list = settlementDayService.selectByDay(localdate);
        System.out.println(list);
    }

    @Test
    void selectSettlementDayByStoreIdAndDayTest() {
        Long storeId = 1L;
        String localdate = "2023-05-08";
        List<SettlementDay> list = settlementDayService.selectByStoreIdAndDay(storeId,localdate);
        System.out.println(list);
        Assertions.assertEquals(0,list.size());
    }

    @Test
    void selectSettlementMonthByStoreIdTest() {
        Long storeId = 1L;
        List<SettlementMonth> settlementMonthListByStoreId = settlementMonthRepository.findByStoreId(storeId);
        System.out.println(settlementMonthListByStoreId);
        // store_id가 안불러진다.
    }

    @Test
    void selectSettlementMonthByDayTest() {
        LocalDate localdate = LocalDate.parse("2023-04-01");
        List<SettlementMonth> settlementMonthListByDay = settlementMonthRepository.findBySettlementDate(localdate);
        System.out.println(settlementMonthListByDay);
    }

    @Test
    void selectSettlementMonthByStoreIdAndDayTest() {
        Long storeId = 1L;
        LocalDate localdate = LocalDate.parse("2023-05-08");
        List<SettlementMonth> settlementMonthListByStoreIdAndDay = settlementMonthRepository.findByStoreIdAndSettlementDate(storeId,localdate);
        System.out.println(settlementMonthListByStoreIdAndDay);
    }
    @Test
    @DisplayName("날짜 형식이 잘못된 경우")
    void selectSettlementDayByDayTest2() {
        String localdate = "2023-05-08";
        List<SettlementDay> settlementDayList = settlementDayService.selectByDay(localdate);
        System.out.println(settlementDayList);
//        Assertions.assertThrows(IllegalArgumentException.class,throw new RuntimeException);
    }
}
