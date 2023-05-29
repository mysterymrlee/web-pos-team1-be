package com.ssg.webpos.service;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    // OrderId로 orders 조회
    public List<Order> selectByOrderId(Long orderId) {
        try{
            List<Order> lists = orderRepository.findOrderById(orderId);
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // "yyyy-mm-dd" 형식의 String 타입 orderDate와 storeId로 주문내역 조회
    public List<Order> selectByPos_StoreIdAndOrderDate(Long storeId, String orderDate) {
        try {
            LocalDateTime date = LocalDateTime.parse(orderDate);
            List<Order> lists = orderRepository.findOrderByPos_StoreIdAndOrderDate(storeId,date);
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // "yyyy-mm" string 받으면 해당 월의 주문 내역 조회
    public List<Order> selectByOrdersMonth(Long storeId, String date) {
        try {
            // "yyyy-mm" 형식의 문자열을 파싱하여 해당 월의 시작일과 마지막 일자 계산
            LocalDate monthStart = LocalDate.parse(date + "-01");
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
            LocalDateTime startDate = LocalDateTime.of(monthStart, LocalTime.MIN);
            LocalDateTime endDate = LocalDateTime.of(monthEnd, LocalTime.MAX);
            System.out.println(startDate);
            System.out.println(endDate);

            List<Order> lists = orderRepository.findOrderByPos_StoreIdAndOrderDateBetween(storeId,startDate,endDate);
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // "yyyy-mm-dd" string 받으면 해당 일의 주문 내역 조회
    public List<Order> selectByOrdersDay(Long storeId, String date) {
        try {
            // "yyyy-mm-dd" 형식의 문자열을 파싱하여 해당 월의 시작일과 마지막 일자 계산
            LocalDateTime start = LocalDateTime.parse(date + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(date + "T23:59:59");

            List<Order> lists = orderRepository.findOrderByPos_StoreIdAndOrderDateBetween(storeId, start, end);
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }




}
