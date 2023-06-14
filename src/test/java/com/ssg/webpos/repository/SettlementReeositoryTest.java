package com.ssg.webpos.repository;

import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.dto.hqSale.HqSettlementDayDTO;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import com.ssg.webpos.service.hqController.method.SaleMethodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class SettlementReeositoryTest {
    @Autowired
    SettlementMonthRepository settlementMonthRepository;
    @Autowired
    SettlementDayRepository settlementDayRepository;

    @Autowired
    SaleMethodService saleMethodService;

    @Test
    void contextVoid() {
        LocalDate start = LocalDate.parse("2023-02-01");
        LocalDate end = LocalDate.parse("2023-03-01");
        List<SettlementMonth> lists = settlementMonthRepository.findBySettlementDateBetween(start,end);
        for(SettlementMonth list: lists) {
            System.out.println(list);
        }
    }

    @Test
    void sumOfYesterdaySettlementPrice() {
        int sum = settlementDayRepository.sumOfAllSettlementPrice();
        System.out.println(sum);
    }

    @Test
    void sumOfYesterdayCharge() {
        int sum = settlementDayRepository.sumOfAllCharge();
        System.out.println(sum);
    }

    @Test
    void sumOfYesterdayProfit() {
        int sum = settlementDayRepository.sumOfAllProfit();
        System.out.println(sum);
    }


    @Test
    void settlementDaySettlementPrice() {
        int sumBystoreId1 = settlementDayRepository.settlementDaySettlementPrice(1);
        System.out.println(sumBystoreId1);
    }

    @Test
    void settlementDayCharge() {
        int sumBystoreId1 = settlementDayRepository.settlementDayCharge(1);
        System.out.println(sumBystoreId1);
    }

    @Test
    void settlementDayProfit() {
        int sumBystoreId1 = settlementDayRepository.settlementDayProfit(1);
        System.out.println(sumBystoreId1);
    }
    // 이번주 모든 백화점 매출합
    @Test
    void sumOfThisWeekAllSettlementPrice() {
        int sum = settlementDayRepository.sumOfThisWeekAllSettlementPrice();
        System.out.println(sum);
    }
    // 이번주 모든 백화점 수수료합
    @Test
    void sumOfThisWeekAllSettlemetCharge() {
        int sum = settlementDayRepository.sumOfThisWeekAllSettlemetCharge();
        System.out.println(sum);
    }
    // 이번주 모든 백화점 영업이익합
    @Test
    void sumOfThisWeekAllSettlemetProfit() {
        int sum = settlementDayRepository.sumOfThisWeekAllSettlemetProfit();
        System.out.println(sum);
    }
    // 이번주 store_id별 백화점 매출합
    @Test
    void sumOfThisWeekAllSettlementPriceByStoreId() {
        int sum = settlementDayRepository.sumOfThisWeekAllSettlementPriceByStoreId(1);
        System.out.println(sum);
    }
    // 이번주 store_id별 백화점 수수료합
    @Test
    void sumOfThisWeekAllSettlemetChargeByStoreId() {
        int sum = settlementDayRepository.sumOfThisWeekAllSettlemetChargeByStoreId(1);
        System.out.println(sum);
    }
    // 이번주 store_id별 백화점 영업이익합
    @Test
    void sumOfThisWeekAllSettlemetProfitByStoreId() {
        int sum = settlementDayRepository.sumOfThisWeekAllSettlemetProfitByStoreId(1);
        System.out.println(sum);
    }
    // 이번달 모든 백화점 매출합
    @Test
    void sumOfThisMonthSettlementPrice() {
        int sum = settlementDayRepository.sumOfThisMonthSettlementPrice();
        System.out.println(sum);
    }
    // 이번달 모든 백화점 수수료합
    @Test
    void sumOfThisMonthCharge() {
        int sum = settlementDayRepository.sumOfThisMonthCharge();
        System.out.println(sum);
    }
    // 이번달 모든 백화점 영업이익합
    @Test
    void sumOfThisMonthProfit() {
        int sum = settlementDayRepository.sumOfThisMonthProfit();
        System.out.println(sum);
    }
    // 이번달 store_id별 백화점 매출합
    @Test
    void sumOfThisMonthSettlementPriceAndStoreId() {
        int sum = settlementDayRepository.sumOfThisMonthSettlementPriceAndStoreId(1);
        System.out.println(sum);
    }
    // 이번달 store_id별 백화점 수수료합
    @Test
    void sumOfThisMonthChargeAndStoreId() {
        int sum = settlementDayRepository.sumOfThisMonthChargeAndStoreId(1);
        System.out.println(sum);
    }
    // 이번달 store_id별 백화점 영업이익합
    @Test
    void sumOfThisMonthProfitAndStoreId() {
        int sum = settlementDayRepository.sumOfThisMonthProfitAndStoreId(1);
        System.out.println(sum);
    }
    // 올해 모든 백화점 매출합
    // 올해 모든 백화점 수수료합
    // 올해 모든 백화점 영업이익합
    // 올해 store_id별 매출합
    // 올해 store_id별 수수료합
    // 올해 store_id별 영업이익합

    @Test
    void test1() {
        LocalDateTime now1 = LocalDateTime.now();
        int a = settlementDayRepository.test1();
        LocalDateTime now2 = LocalDateTime.now();

        Duration duration = Duration.between(now1, now2);
        long nanoseconds1 = duration.toNanos();
        System.out.println(" : " + nanoseconds1);

        LocalDateTime now3 = LocalDateTime.now();
        int b = settlementDayRepository.test2();
        LocalDateTime now4 = LocalDateTime.now();

        Duration duration1 = Duration.between(now3, now4);
        long nanoseconds2 = duration1.toNanos();
        System.out.println("orders로 모든 주문 가격을 합했을 때 걸리는 시간 : " + nanoseconds2);

        LocalDateTime now5 = LocalDateTime.now();
        int c = settlementDayRepository.test3a();
        int d = settlementDayRepository.test3b();
        int total = c+d;
        LocalDateTime now6 = LocalDateTime.now();

        Duration duration2 = Duration.between(now5, now6);
        long nanoseconds = duration2.toNanos();
        System.out.println("settlement_day로 구한 모든 주문 부분 가격 + settlement_month로 구한 모든 주문 부분 가격을 합했을 때 걸리는 시간 : " + nanoseconds);
    }

    @Test
    @Transactional
    void settlementDayBetweenYesterdayAgoAndYesterday() {
        List<SettlementDay> list = settlementDayRepository.selectSettlementDayBetweenYesterday1WeekAgoAndYesterdayByStoreId(1);
        List<HqSettlementDayDTO> hqSettlementDayDTOList = saleMethodService.HqSaleMethods(list);
        for (HqSettlementDayDTO hqSettlementDayDTO : hqSettlementDayDTOList) {
            System.out.println(hqSettlementDayDTO);
        }
    }

    @Test
    void test() {
        List<Object[]> list = settlementDayRepository.settlementDay1Month();
        for (Object[] objects : list) {
            for (Object object : objects) {
                System.out.print(object + " ");
            }
            System.out.println();
        }

    }

}
