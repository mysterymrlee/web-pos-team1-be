package com.ssg.webpos.service.managerController;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.PointSaveHistory;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.repository.PointSaveHistoryRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional
public class CancelOrderService {
    private final OrderRepository orderRepository;
    private final PointSaveHistoryRepository pointSaveHistoryRepository;

    public void cancelOrder(String serialNumber) {
        // 새로운 열이 생성되지 않고 기존 열이 수정되는 관계로 쿼리문 사용
        // 기존 결제내역 할당
        Order order = orderRepository.findBySerialNumber(serialNumber);

        System.out.println(order);
        // 결제 취소내역 열 생성

        // 기존 결제내역과 같은 필드
        Order cancelOrder = new Order();
        cancelOrder.setId(order.getId());
        cancelOrder.setSerialNumber(order.getSerialNumber());
        cancelOrder.setPos(order.getPos());
        cancelOrder.setOrderDate(order.getOrderDate());
        cancelOrder.setPayMethod(order.getPayMethod());
        cancelOrder.setOrderName(order.getOrderName());
        cancelOrder.setCardName(order.getCardName());
        cancelOrder.setCardNumber(order.getCardNumber());
        cancelOrder.setMerchantUid(order.getMerchantUid());
        // 기존 결제내역과 다른 필드
        cancelOrder.setOrderStatus(OrderStatus.CANCEL);
        int totalPrice = order.getTotalPrice();
        cancelOrder.setTotalPrice(-totalPrice);
        int couponUsePrice = order.getCouponUsePrice();
        cancelOrder.setCouponUsePrice(-couponUsePrice);
        int pointUsePrice = order.getPointUsePrice();
        cancelOrder.setPointUsePrice(-pointUsePrice);
        int finalTotalPrice = order.getFinalTotalPrice();
        cancelOrder.setFinalTotalPrice(-finalTotalPrice);
        int charge = order.getCharge();
        cancelOrder.setCharge(-charge);
        System.out.println(cancelOrder);

        // point_use_history에 새로운 열 생성(포인트 사용 취소)
        PointSaveHistory pointSaveHistory = new PointSaveHistory();
        pointSaveHistory.setPointStatus((byte) 1);
        pointSaveHistory.setOrder(order);
        // 포인트 아이디랑 연결하는 곳에서 문제
        pointSaveHistory.setPointSaveAmount(-pointUsePrice);
        System.out.println(pointSaveHistory);
        pointSaveHistoryRepository.save(pointSaveHistory);
        // 포인트 트리거 문서에서 point_save_history에 열이 생기면 트리거
        // 만약 order_status가 cancel인 열이 새로 생기면 자동으로 포인트 트리거가 발생하도록
        orderRepository.save(cancelOrder);
        // point_use_history에서 새로운 열 생성

    }
}
