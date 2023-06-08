package com.ssg.webpos.repository.order;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Pos;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.PosStoreCompositeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
  // 어제의 모든 백화점 구매 주문수
  @Query(value = "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
  int countOrdersByYesterday();

  // 어제의 store_id별 백화점 구매 주문수
  @Query(value = "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) and store_id = :storeId ", nativeQuery = true)
  int countOrdersByYesterdayAndStoreID(@Param("storeId") int storeId);

  // 이번주의 모든 백화점 구매 주문수
  @Query(value = "SELECT COUNT(*) FROM orders WHERE WEEK(order_date, 1) = WEEK(CURDATE())", nativeQuery = true)
  int countOrderByThisWeek();

  // 이번주의 store_id별 백화점 구매 주문수
  @Query(value = "SELECT COUNT(*) FROM orders WHERE WEEK(order_date, 1) = WEEK(CURDATE()) AND store_id = :storeId", nativeQuery = true)
  int countOrderByThisWeekAndStoreId(@Param("storeId") int storeID);

  // 이번달의 모든 백화점 구매 주문수
  @Query(value = "SELECT COUNT(*)  FROM orders WHERE MONTH(order_date) = MONTH(CURDATE())", nativeQuery = true)
  int countOrderByThisMonth();
  // 이번달의 store_id별 백화점 구매 주문수
  @Query(value = "SELECT COUNT(*)  FROM orders WHERE MONTH(order_date) = MONTH(CURDATE()) AND store_id = :storeId", nativeQuery = true)
  int countOrderByThisMonthByStoreId(@Param("storeId") int storeID);
  // 올해 모든 백화점 구매 주문수
  @Query(value = "SELECT COUNT(*)  FROM orders WHERE YEAR (order_date) = YEAR (CURDATE())", nativeQuery = true)
  int countOrderByThisYear();
  // 올해 store_id별 백화점 구매 주문수
  @Query(value = "SELECT COUNT(*)  FROM orders WHERE YEAR (order_date) = YEAR (CURDATE()) AND store_id = :storeId", nativeQuery = true)
  int countOrderByThisYearAndStoreId(@Param("storeId") int storeID);
  //올해

  // 올해 모든 백화점 매출합
  @Query(value = "select sum(final_total_price) from orders", nativeQuery = true)
  int sumOfAllSettlementPrice();
  // 올해 모든 백화점 수수료합
  @Query(value = "select sum(charge) from orders", nativeQuery = true)
  int sumOfAllCharge();
  // 올해 모든 백화점 영업이익합
  @Query(value = "select sum(profit) from orders", nativeQuery = true)
  int sumOfAllProfit();
  // 올해 store_id별 매출합
  @Query(value = "select sum(final_total_price) from orders where store_id = :storeId", nativeQuery = true)
  int sumOfAllSettlementPriceByStoreId(@Param("storeId") int storeId);
  // 올해 store_id별 수수료합
  @Query(value = "select sum(charge) from orders where store_id = :storeId", nativeQuery = true)
  int sumOfAllChargeByStoreId(@Param("storeId") int storeId);
  // 올해 store_id별 영업이익합
  @Query(value = "select sum(profit) from orders where store_id = :storeId", nativeQuery = true)
  int sumOfAllProfitByStoreId(@Param("storeId") int storeId);

  Order findByDeliveryId(Long id);
}
