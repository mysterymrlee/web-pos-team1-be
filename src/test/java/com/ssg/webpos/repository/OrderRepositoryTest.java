package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Order;
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

}
