package com.ssg.webpos.service;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Point;
import com.ssg.webpos.domain.PointUseHistory;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.domain.enums.RoleUser;
import com.ssg.webpos.dto.point.PointDTO;
import com.ssg.webpos.repository.PointRepository;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class PointServiceTest {

  @Autowired
  private PointService pointService;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CartRedisRepository cartRedisRepository;
  @Autowired
  private PointRepository pointRepository;
  @Autowired
  private PointUseHistoryRepository pointUseHistoryRepository;
  @Autowired
  private CartService cartService;
  @Autowired
  private OrderRepository orderRepository;

  @Test
  @DisplayName("유효한 사용자 ID로 포인트 조회하기")
  void getPointAmount_ValidUserId_ReturnsPointAmount() {
    int expectedPointAmount = 100;
    Point point = new Point();
    point.setPointAmount(expectedPointAmount);
    pointRepository.save(point);

    User user = new User();
    user.setName("홍길동");
    user.setEmail("1111@naver.com");
    user.setPhoneNumber("01099999999");
    user.setPassword("1234");
    user.setRole(RoleUser.NORMAL);
    userRepository.save(user);

    user.setPoint(point);
    userRepository.save(user);

    Long userId = user.getId();

    int actualPointAmount = pointService.getPointAmount(userId);

    assertEquals(expectedPointAmount, actualPointAmount);
  }

  @Test
  @DisplayName("유효하지 않은 사용자 ID로 포인트 조회 시 RuntimeException 발생")
  void getPointAmount_InvalidUserId_ThrowsRuntimeException() {
    Long userId = 999L;
    // Assert
    assertThrows(RuntimeException.class, () -> pointService.getPointAmount(userId));
  }

  @Test
  @DisplayName("유효한 사용자 ID와 총 가격으로 포인트 업데이트하기")
  void updatePoint_ValidUserIdAndTotalPrice_ReturnsEarnedPoint() {
    int totalPrice = 1000;
    int expectedEarnedPoint = 1;
    User user = new User();
    user.setName("홍길동");
    user.setEmail("1111@naver.com");
    user.setPhoneNumber("01099999999");
    user.setPassword("1234");
    user.setRole(RoleUser.NORMAL);
    Point point = new Point();
    point.setPointAmount(0);
    user.setPoint(point);
    userRepository.save(user);
    Long userId = user.getId();

    int actualEarnedPoint = pointService.updatePoint(totalPrice);

    assertEquals(expectedEarnedPoint, actualEarnedPoint);
  }

}