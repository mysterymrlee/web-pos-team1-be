package com.ssg.webpos.repository.order;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Pos;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.PosStoreCompositeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
  // Order에서 List<Order>로 변경
  // Id로 조회
  List<Order> findOrderById(Long id);
  List<Order> findAll();
  // pos의 store_id로 조회
  List<Order> findByPos_StoreId(Long storeId);
//  @Query("SELECT o FROM Order o WHERE o.pos.storeId = :storeId AND o.orderDate LIKE CONCAT(:orderDate, '%')")
//  List<Order> findOrderByPos_StoreIdAndOrderDate(Long storeId, String orderDate);
  // pos의 store_id와 orderDate 조회
  List<Order> findOrderByPos_StoreIdAndOrderDate(Long storeId, LocalDateTime orderDate);
  // 기간 쿼리 조회하기
  List<Order> findOrderByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
  // store_id,startDate,endDate
  List<Order> findOrderByPos_StoreIdAndOrderDateBetween(Long storeId, LocalDateTime startDate, LocalDateTime endDate);
  Order findBySerialNumber(String serialNumber);

}
