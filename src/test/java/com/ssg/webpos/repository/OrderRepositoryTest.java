package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.repository.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class OrderRepositoryTest {
    @Autowired
    OrderRepository orderRepository;

    @Test
    void contextLoads() {
        List<Order> list = orderRepository.findAll();
        for (Order order: list) {
            System.out.println(order);
        }
    }

    @Test
    void fidnBySerialNumber() {
        Order order = orderRepository.findBySerialNumber("20230523001");
        System.out.println(order);
    }

    @Test
    void countByYesterday() {
        int count = orderRepository.countOrdersByYesterday();
        System.out.println(count);
    }

    @Test
    void countByYesterdayAndStoreId() {
        int count = orderRepository.countOrdersByYesterdayAndStoreID(2);
        System.out.println(count);
    }

    // 이번주의 모든 백화점의 주문수
    @Test
    void countByThisWeek() {
        int count = orderRepository.countOrderByThisWeek();
        System.out.println(count);
    }

    // 이번주의 store_id별 주문수
    @Test
    void countByThisWeekAndStoreID() {
        int count = orderRepository.countOrderByThisWeekAndStoreId(1);
        System.out.println(count);
    }

    // 이번달의 모든 백화점의 주문수
    @Test
    void countBythisMonth() {
        int count = orderRepository.countOrderByThisMonth();
        System.out.println(count);
    }

    // 이번달의 store_id별 주문수
    @Test
    void countBythisMonthAndStoreId() {
        int count = orderRepository.countOrderByThisMonthByStoreId(2);
        System.out.println(count);
    }
    // 올해의 모든 백화점 주문수
    @Test
    void countBythisYear() {
        int count = orderRepository.countOrderByThisYear();
        System.out.println(count);
    }
    // 올해의 store_id별 주문수
    @Test
    void countBythisYearAndStoreId() {
        int count = orderRepository.countOrderByThisYearAndStoreId(2);
        System.out.println(count);
    }


    // 올해 모든 백화점 매출합
    @Test
    void sumOfAllSettlementPrice() {
        int count = orderRepository.sumOfAllSettlementPrice();
        System.out.println(count);
    }
    // 올해 모든 백화점 수수료합
    @Test
    void sumOfAllCharge() {
        int count = orderRepository.sumOfAllCharge();
        System.out.println(count);
    }
    // 올해 모든 백화점 영업이익합
    @Test
    void sumOfAllProfit() {
        int count = orderRepository.sumOfAllProfit();
        System.out.println(count);
    }
    // 올해 store_id별 매출합
    @Test
    void sumOfAllSettlementPriceByStoreId() {
        int count = orderRepository.sumOfAllSettlementPriceByStoreId(1);
        System.out.println(count);
    }
    // 올해 store_id별 수수료합
    @Test
    void sumOfAllChargeByStoreId() {
        int count = orderRepository.sumOfAllChargeByStoreId(1);
        System.out.println(count);
    }
    // 올해 store_id별 영업이익합
    @Test
    void sumOfAllProfitByStoreId() {
        int count = orderRepository.sumOfAllProfitByStoreId(1);
        System.out.println(count);
    }

    @Test
    void countOrderByThisWeekBeweenYesterdayAndYesterday1WeekAgo() {
        int count = orderRepository.countOrderByThisWeekBeweenYesterdayAndYesterday1WeekAgo();
        System.out.println(count);
    }

    @Test
    void sumOfFinalOrderPriceBetweenYesterday1WeekAgoAndYesterday() {
        int count = orderRepository.sumOfFinalOrderPriceBetweenYesterday1WeekAgoAndYesterday();
        System.out.println(count);
    }

    @Test
    void sumOfChargeBetweenYesterday1WeekAgoAndYesterday() {
        int count = orderRepository.sumOfChargeBetweenYesterday1WeekAgoAndYesterday();
        System.out.println(count);
    }

    @Test
    void sumOfProfitBetweenYesterday1WeekAgoAndYesterday() {
        int count = orderRepository.sumOfProfitBetweenYesterday1WeekAgoAndYesterday();
        System.out.println(count);
    }

    // store_id별 어제 일주일전부터 어제까지의 주문수, 매출합, 수수료합, 영업이익합
    @Test
    void countOrderByThisWeekBeweenYesterdayAndYesterday1WeekAgoBystoreId() {
        int count = orderRepository.countOrderByThisWeekBeweenYesterdayAndYesterday1WeekAgoBystoreId(1);
        System.out.println(count);
    }
    @Test
    void sumOfFinalOrderPriceBetweenYesterday1WeekAgoAndYesterdayByStoreId() {
        int count = orderRepository.sumOfFinalOrderPriceBetweenYesterday1WeekAgoAndYesterdayByStoreId(1);
        System.out.println(count);
    }
    @Test
    void sumOfChargeBetweenYesterday1WeekAgoAndYesterdayByStoreId() {
        int count = orderRepository.sumOfChargeBetweenYesterday1WeekAgoAndYesterdayByStoreId(1);
        System.out.println(count);
    }@Test
    void sumOfProfitBetweenYesterday1WeekAgoAndYesterdayByStoreId() {
        int count = orderRepository.sumOfProfitBetweenYesterday1WeekAgoAndYesterdayByStoreId(1);
        System.out.println(count);
    }
    @Test
    void test() {
        Order order = orderRepository.findBySerialNumber("20230601013");
        System.out.println(order);
    }

    @Test
    void test1() {
        try {
            Order order = orderRepository.findByMerchantUid("202306011011410202");
            System.out.println(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testForInsert() {
        try {
            LocalDateTime createdDate = LocalDateTime.now();
            LocalDateTime lastModifiedDate = LocalDateTime.now();

            orderRepository.insertOrderCancel(3,3,3,"CANCEL","CREDIT_CARD",
                    3,3,3,3,"2032-12-01T00:00:01",createdDate,lastModifiedDate, (long) 1, (long)1,
                    (long)1, null,"1313");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testForSuccessReceipt() {
        try {
            Order order = orderRepository.findByMerchantUidAndOrderStatus("202306011011410202", OrderStatus.SUCCESS);
            System.out.println(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testForDESC() {
        try {
            Order order = orderRepository.findFirstByMerchantUidAndOrderStatusOrderByOrderDateDesc("202306011011410202", OrderStatus.SUCCESS);
            System.out.println(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
