package com.ssg.webpos.repository.order;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Pos;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.PosStoreCompositeId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {
  Order findByPosId(PosStoreCompositeId id);
  Order findOrderById(Long id);
  Order findOrderByPosId(Long id);

}
