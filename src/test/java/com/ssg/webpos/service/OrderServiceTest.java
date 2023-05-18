package com.ssg.webpos.service;

import com.ssg.webpos.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class OrderServiceTest {
    @Autowired
    OrderService orderService;

    @Test
    @DisplayName("orderId로 조회")
    void selectByOrderId() {
        List<Order> lists = orderService.selectByOrderId(1L);
        for(Order order:lists) {
            System.out.println(order);
        }
    }

    @Test
    @DisplayName("yyyy-mm로 조회")
    void selectByStoreIdAndBetweenMonth() {
        List<Order> lists = orderService.selectByOrdersMonth(1L,"2023-04");

    }

    @Test
    @DisplayName("yyyy-mm-dd로 조회")
    void selectByStoreIdAndBetweenDay() {
        List<Order> lists = orderService.selectByOrdersDay(1L,"2023-05-11");
        for(Order order:lists) {
            System.out.println(order);
        }
    }
}
