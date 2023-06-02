package com.ssg.webpos.repository.cart;

import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
  Cart findByOrderIdAndProductId(Long orderId, Long productId);
  List<Cart> findByOrder(Order order);
  Cart findByProductAndOrder(Product product, Order order);
  List<Cart> findAllByOrderId(Long orderId);
}