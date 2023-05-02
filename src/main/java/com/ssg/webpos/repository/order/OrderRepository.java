package com.ssg.webpos.repository.order;

import com.ssg.webpos.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {
  Order findByPosId(Long id);
  Order findOrderById(Long id);
  Order findOrderByPosId(Long id);
}
