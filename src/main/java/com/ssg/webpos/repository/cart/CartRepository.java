package com.ssg.webpos.repository.cart;

import com.ssg.webpos.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface CartRepository extends JpaRepository<Cart, Long> {
  Cart findByOrderIdAndProductId(Long orderId, Long productId);
  Cart findOrderProductById(Long id);
  List<Cart> findCartProductByProductId(Long id);
}