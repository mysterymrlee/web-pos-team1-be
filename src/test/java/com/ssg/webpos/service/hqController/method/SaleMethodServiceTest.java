package com.ssg.webpos.service.hqController.method;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Pos;
import com.ssg.webpos.domain.SettlementDay;
import com.ssg.webpos.domain.Store;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.hqSale.HqSaleByStoreNameDTO;
import com.ssg.webpos.dto.hqSale.HqSaleOrderDTO;
import com.ssg.webpos.dto.hqSale.HqSettlementDayDTO;
import com.ssg.webpos.repository.settlement.SettlementDayRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SaleMethodServiceTest {
  @Autowired
  SettlementDayRepository settlementDayRepository;
  @Autowired
  StoreRepository storeRepository;
  @Autowired
  SaleMethodService saleMethodService;

  @Test
  void hqSaleMethods() {
    // 테스트용 SettlementDay 객체 리스트 생성
    List<SettlementDay> settlementDayList = new ArrayList<>();

    // SettlementDay 1
    SettlementDay settlementDay1 = new SettlementDay();
    settlementDay1.setSettlementDate(LocalDate.of(2023, 6, 1));
    settlementDay1.setSettlementPrice(1000);
    settlementDayList.add(settlementDay1);

    // SettlementDay 2
    SettlementDay settlementDay2 = new SettlementDay();
    settlementDay2.setSettlementDate(LocalDate.of(2023, 6, 2));
    settlementDay2.setSettlementPrice(2000);
    settlementDayList.add(settlementDay2);

    // SaleMethodService 객체 생성
    SaleMethodService saleMethodService = new SaleMethodService(settlementDayRepository, storeRepository);

    // HqSaleMethods 메소드 호출
    List<HqSettlementDayDTO> result = saleMethodService.HqSaleMethods(settlementDayList);

    // Assertions
    assertEquals(2, result.size());
    assertEquals(LocalDate.of(2023, 6, 1), result.get(0).getSettlementDayDate());
    assertEquals(1000, result.get(0).getSettlementDaySettlementPrice());

    assertEquals(LocalDate.of(2023, 6, 2), result.get(1).getSettlementDayDate());
    assertEquals(2000, result.get(1).getSettlementDaySettlementPrice());
  }

  @Test
  void saleMethod() {
    // 테스트용 Object 배열 리스트 생성
    List<Object[]> objectList = new ArrayList<>();

    // Object 배열 1
    Object[] objects1 = new Object[2];
    objects1[0] = Date.valueOf("2023-06-01");
    objects1[1] = BigDecimal.valueOf(1000);
    objectList.add(objects1);

    // Object 배열 2
    Object[] objects2 = new Object[2];
    objects2[0] = Date.valueOf("2023-06-02");
    objects2[1] = BigDecimal.valueOf(2000);
    objectList.add(objects2);

    // SaleMethodService 객체 생성
    SaleMethodService saleMethodService = new SaleMethodService(settlementDayRepository, storeRepository);

    // saleMethod 메소드 호출
    List<HqSettlementDayDTO> result = saleMethodService.saleMethod(objectList);

    // Assertions
    assertEquals(2, result.size());
    assertEquals(LocalDate.of(2023, 6, 1), result.get(0).getSettlementDayDate());
    assertEquals(1000, result.get(0).getSettlementDaySettlementPrice());

    assertEquals(LocalDate.of(2023, 6, 2), result.get(1).getSettlementDayDate());
    assertEquals(2000, result.get(1).getSettlementDaySettlementPrice());
  }

  @Test
  void testPieChartMethod() {
//    // Create sample input data
//    List<Object[]> objectList = new ArrayList<>();
//    Object[] object1 = {BigDecimal.valueOf(100), BigInteger.valueOf(1)};
//    Object[] object2 = {BigDecimal.valueOf(200), BigInteger.valueOf(2)};
//    objectList.add(object1);
//    objectList.add(object2);
//
//    saleMethodService.pieChartMethod(objectList);
//
////    // Invoke the method
////    List<HqSaleByStoreNameDTO> result = saleMethodService.pieChartMethod(objectList);
////
////    // Assertions
////    assertEquals(2, result.size());
////
////    HqSaleByStoreNameDTO dto1 = result.get(0);
////    assertEquals(100, dto1.getSettlementPrice());
////    assertEquals("Store 1", dto1.getStoreName());
////
////    HqSaleByStoreNameDTO dto2 = result.get(1);
////    assertEquals(200, dto2.getSettlementPrice());
////    assertEquals("Store 2", dto2.getStoreName());
  }


  @Test
  void orderListMethod() {
    // 테스트용 Order 객체 리스트 생성
    List<Order> orderList = new ArrayList<>();

    // Order 1
    Order order1 = new Order();
    order1.setSerialNumber("SN001");
    order1.setOrderDate(LocalDateTime.of(2023, 6, 1, 0, 0, 0));
    order1.setOrderStatus(OrderStatus.SUCCESS);
    order1.setPayMethod(PayMethod.CREDIT_CARD);
    order1.setTotalPrice(10000);
    order1.setPointUsePrice(2000);
    order1.setCouponUsePrice(5000);
    order1.setFinalTotalPrice(3000);
    order1.setCharge(20);
    order1.setTotalOriginPrice(7000);
    order1.setProfit(100);

    // POS 객체와 가짜 Store 객체 생성
    Pos pos1 = new Pos();
    Store store1 = new Store();
    store1.setId(1L);
    store1.setName("Store 1");
    pos1.setStore(store1);
    order1.setPos(pos1);

    orderList.add(order1);

    // Order 2
    Order order2 = new Order();
    order2.setSerialNumber("SN002");
    order2.setOrderDate(LocalDateTime.of(2023, 6, 1, 0, 0, 0));
    order2.setOrderStatus(OrderStatus.SUCCESS);
    order2.setPayMethod(PayMethod.CREDIT_CARD);
    order2.setTotalPrice(10000);
    order2.setPointUsePrice(2000);
    order2.setCouponUsePrice(5000);
    order2.setFinalTotalPrice(3000);
    order2.setCharge(20);
    order2.setTotalOriginPrice(7000);
    order2.setProfit(100);
    // POS 객체와 가짜 Store 객체 생성
    Pos pos2 = new Pos();
    Store store2 = new Store();
    store2.setId(2L);
    store2.setName("Store 2");
    pos2.setStore(store2);
    order2.setPos(pos2);

    orderList.add(order2);

// SaleMethodService 객체 생성
    SaleMethodService saleMethodService = new SaleMethodService(settlementDayRepository, storeRepository);

// orderListMethod 메소드 호출
    List<HqSaleOrderDTO> result = saleMethodService.orderListMethod(orderList);

// Assertions
    assertEquals(2, result.size());

    assertEquals("SN001", result.get(0).getSerialNumber());
    assertEquals("Store 1", result.get(0).getStoreName());
    assertEquals(LocalDateTime.of(2023, 6, 1, 0, 0, 0), result.get(0).getOrderDate());
    assertEquals(OrderStatus.SUCCESS, result.get(0).getOrderStatus());
    assertEquals(PayMethod.CREDIT_CARD, result.get(0).getPayMethod());
    assertEquals(10000, result.get(0).getTotalPrice());
    assertEquals(2000, result.get(0).getPointUsePrice());
    assertEquals(5000, result.get(0).getCouponUsePrice());
    assertEquals(3000, result.get(0).getFinalTotalPrice());
    assertEquals(20, result.get(0).getCharge());
    assertEquals(7000, result.get(0).getTotalOriginPrice());
    assertEquals(100, result.get(0).getProfit());

    assertEquals("SN002", result.get(1).getSerialNumber());
    assertEquals("Store 2", result.get(1).getStoreName());
    assertEquals(LocalDateTime.of(2023, 6, 1, 0, 0, 0), result.get(1).getOrderDate());
    assertEquals(OrderStatus.SUCCESS, result.get(1).getOrderStatus());
    assertEquals(PayMethod.CREDIT_CARD, result.get(1).getPayMethod());
    assertEquals(10000, result.get(1).getTotalPrice());
    assertEquals(2000, result.get(1).getPointUsePrice());
    assertEquals(5000, result.get(1).getCouponUsePrice());
    assertEquals(3000, result.get(1).getFinalTotalPrice());
    assertEquals(20, result.get(1).getCharge());
    assertEquals(7000, result.get(1).getTotalOriginPrice());
    assertEquals(100, result.get(1).getProfit());




  }
}