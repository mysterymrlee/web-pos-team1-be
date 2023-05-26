package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.repository.cart.CartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CartRepositoryTest {
    @Autowired
    CartRepository cartRepository;
    @Test
    @DisplayName("orderId로 카트엔티티목록 조회 반환")
    void findAllByOrderId() {
        List<Cart> cartList = cartRepository.findAllByOrderId(99L);
        for(Cart cart:cartList) {
            System.out.println(cart);
        }

    }
}
