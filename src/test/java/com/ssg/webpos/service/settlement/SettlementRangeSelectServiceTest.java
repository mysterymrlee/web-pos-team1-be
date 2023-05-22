package com.ssg.webpos.service.settlement;

import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.service.SettlementDayService;
import com.ssg.webpos.service.SettlementMonthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SettlementRangeSelectServiceTest {
    @Autowired
    SettlementDayService settlementDayService;
    @Autowired
    SettlementMonthService settlementMonthService;

    @Test
    @DisplayName("일별 정산내역의 기간별 조회 테스트")     // 설정한 모든 기간내 정산내역이 있지 않더라도 조회 가능
    void SettlementDayRangeTest() {
        Long storeId = 1L;
        String StartDate = "2023-04-09";
        String EndDate = "2023-04-11";
        List<SettlementDay> lists = settlementDayService.selectByStoreIdAndDayBetween(storeId,StartDate,EndDate);
        for(SettlementDay list:lists) {
            System.out.println(list);
        }
        Assertions.assertEquals(0,lists.size());
    }


}
