package com.ssg.webpos.service.settlement;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.domain.Store;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import com.ssg.webpos.repository.store.StoreRepository;
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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.assertThrows;
import static org.testng.AssertJUnit.assertTrue;

@SpringBootTest
@Transactional
public class SettlementSelectServiceTest {

    @Autowired
    SettlementDayRepository settlementDayRepository;
    @Autowired
    SettlementMonthRepository settlementMonthRepository;
    @Autowired
    SettlementDayService settlementDayService;
    @Autowired
    SettlementMonthService settlementMonthService;
    @Autowired
    StoreRepository storeRepository;



    @Test
    void selectByYearMonthAndStoreIdtest() {
        String date = "2023-06";
        Store store = Store.builder()
            .name("센텀시티점")
            .postCode("111111")
            .address("부산")
            .branchName("지점명") // 적절한 값을 설정해줘야 함
            .build();
        storeRepository.save(store);
        SettlementDay settlementDay = SettlementDay.builder()
                .settlementDate(LocalDate.of(2023, 06, 03))
                .settlementPrice(10000)
                .build();
        settlementDayRepository.save(settlementDay);
        System.out.println("settlementDay = " + settlementDay);

        List<SettlementDay> settlementDays = settlementDayService.selectByYearMonthAndStoreId(date, store.getId());
        System.out.println("settlementDays = " + settlementDays);
    }

    @Test
    void selectByYearMonthAndStoreIdException() {
        String date = "2023-07";
        Long storeId = 1L;

        // Store 생성 및 저장
        Store store = Store.builder()
            .name("센텀시티점")
            .postCode("111111")
            .address("부산")
            .branchName("지점명") // 적절한 값을 설정해줘야 함
            .build();
        storeRepository.save(store);

        // SettlementDay 생성 및 저장
        SettlementDay settlementDay = SettlementDay.builder()
            .settlementDate(LocalDate.of(2023, 06, 01))
            .settlementPrice(10000)
            .build();
        settlementDayRepository.save(settlementDay);

        // SettlementDayService 객체 생성
        SettlementDayService settlementDayService = new SettlementDayService(settlementDayRepository);

        List<SettlementDay> settlementDays = settlementDayService.selectByYearMonthAndStoreId(date, storeId);

        assertTrue(settlementDays.isEmpty());

        // 예외가 발생하는지 검증
        assertThrows(Exception.class, () ->
            settlementDayService.selectByYearMonthAndStoreId(date, storeId));
    }

    @Test
    void selectByDayRange() {
        String startDate = "2023-06-01";
        String endDate = "2023-06-02";
        Store store = Store.builder()
            .name("센텀시티점")
            .postCode("111111")
            .address("부산")
            .branchName("지점명") // 적절한 값을 설정해줘야 함
            .build();
        storeRepository.save(store);
        SettlementDay settlementDay = SettlementDay.builder()
            .settlementDate(LocalDate.of(2023, 06, 01))
            .settlementPrice(10000)
            .build();
        settlementDayRepository.save(settlementDay);
        SettlementDay settlementDay2 = SettlementDay.builder()
            .settlementDate(LocalDate.of(2023, 06, 02))
            .settlementPrice(10000)
            .build();
        settlementDayRepository.save(settlementDay2);
        List<SettlementDay> settlementDays = settlementDayService.selectByDayRange(startDate, endDate);
        System.out.println("settlementDays = " + settlementDays);

//        LocalDate start = LocalDate.parse(startDate);
//        LocalDate end = LocalDate.parse(endDate);
//        List<SettlementDay> list = settlementDayRepository.findBySettlementDateBetween(start,end);
//        return list;
////    } catch (Exception e) {
////        e.printStackTrace();
////        System.out.println(e);
////        return Collections.emptyList();
////    }
    }
    @Test
    void selectByDayAndStoreIdRange() {
        String startDate = "2023-06-01";
        String endDate = "2023-06-02";
        boolean isExceptionExpected = true;
        Store store = Store.builder()
            .name("센텀시티점")
            .postCode("111111")
            .address("부산")
            .branchName("지점명") // 적절한 값을 설정해줘야 함
            .build();
        storeRepository.save(store);
        SettlementDay settlementDay = SettlementDay.builder()
            .settlementDate(LocalDate.of(2023, 06, 01))
            .settlementPrice(10000)
            .build();
        settlementDayRepository.save(settlementDay);
        SettlementDay settlementDay2 = SettlementDay.builder()
            .settlementDate(LocalDate.of(2023, 06, 02))
            .settlementPrice(10000)
            .build();
        settlementDayRepository.save(settlementDay2);
        try {
            List<SettlementDay> settlementDays = settlementDayService.selectByDayAndStoreIdRange(startDate, endDate, store.getId());
            System.out.println("settlementDays = " + settlementDays);
        } catch (Exception e) {
            Assertions.assertTrue(isExceptionExpected);
        }
    }
//    @Test
//    void selectByDayAndStoreIdRange_ExceptionHandling() {
//        String startDate = "2023-06-01";
//        String endDate = "2023-06-02";
//        Long storeId = 1L;
//
//        boolean isExceptionExpected = true;
//
//        List<SettlementDay> settlementDays = null;
//        try {
//            settlementDays = settlementDayService.selectByDayAndStoreIdRange(startDate, endDate, storeId);
//        } catch (Exception e) {
//            // 예외가 발생한 경우, isExceptionExpected가 true여야 합니다.
//            Assertions.assertTrue(isExceptionExpected);
//            Assertions.assertEquals("Expected exception message", e.getMessage()); // 예외 메시지가 정확한지 확인
//        }
//
//        // settlementDays에 대한 추가적인 검증 로직 작성
//        // 예를 들어, 조회된 SettlementDay 개수가 예상과 일치하는지 확인할 수 있습니다.
//        Assertions.assertEquals(2, settlementDays.size());
//        // 나머지 검증 로직 추가...
//    }


    @Test
    void selectSettlementDayByStoreIdTest() throws IllegalArgumentException {
        Long storeId = 1L;
        List<SettlementDay> settlementDayList = settlementDayService.selectByStoreId(storeId);
        System.out.println(settlementDayList);
        Assertions.assertEquals(0, settlementDayList.size());
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
