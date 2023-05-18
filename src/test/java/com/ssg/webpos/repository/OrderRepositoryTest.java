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
        Assert.assertEquals(7,list.size());
    }

}
