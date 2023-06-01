package com.ssg.webpos.service;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.RoleUser;
import com.ssg.webpos.dto.point.PointUseDTO;
import com.ssg.webpos.repository.PointRepository;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class PointUseHistoryServiceTest {
  @Autowired
  PointUseHistoryService pointUseHistoryService;
  @Autowired
  PointUseHistoryRepository pointUseHistoryRepository;
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  PointRepository pointRepository;

  @Test
  void savePointUseHistory() {
    // Given
    User user = new User();
    user.setName("하경");
    user.setRole(RoleUser.NORMAL);
    user.setPassword("1111");
    user.setEmail("yhk@naver.com");
    user.setPhoneNumber("01011111111");

    PointUseDTO pointUseDTO = new PointUseDTO();
    pointUseDTO.setAmount(100);

    Point point = new Point();
    point.setPointAmount(pointUseDTO.getAmount());
    user.setPoint(point);
    userRepository.save(user);
    // When
    PointUseHistory pointUseHistory = new PointUseHistory();
    pointUseHistory.setPointUseAmount(pointUseDTO.getAmount());
    pointUseHistory.setPoint(point);
    pointUseHistoryService.savePointUseHistory(pointUseHistory);

    // Then
    PointUseHistory savedPointHistory = pointUseHistoryRepository.findById(pointUseHistory.getId()).orElse(null);
    System.out.println("savedPointHistory = " + savedPointHistory);
    assertNotNull(savedPointHistory);
    assertEquals(pointUseHistory.getPointUseAmount(), savedPointHistory.getPointUseAmount());
  }
}