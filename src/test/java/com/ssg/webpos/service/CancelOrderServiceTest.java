package com.ssg.webpos.service;

import com.ssg.webpos.service.managerController.CancelOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CancelOrderServiceTest {
    @Autowired
    CancelOrderService cancelOrderService;

    @Test
    void test() {
        cancelOrderService.cancelOrder("20230601013");
    }
}
