package com.ssg.webpos.service;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.RoleUser;
import com.ssg.webpos.dto.point.PointUseDTO;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

  @Test
  void savePointHistory() {
    // Given
    User user = new User();
    user.setPoint(2000);
    user.setName("하경");
    user.setRole(RoleUser.NORMAL);
    user.setPassword("1111");
    user.setEmail("yhk@naver.com");
    user.setPhoneNumber("01011111111");
    userRepository.save(user);

    PointUseDTO pointUseDTO = new PointUseDTO();
    pointUseDTO.setAmount(100);

    // When
    PointUseHistory pointHistory = new PointUseHistory();
    pointHistory.setAmount(pointUseDTO.getAmount());
    pointHistory.setUser(user);
    pointUseHistoryService.savePointUseHistory(pointHistory);

    // Then
    PointUseHistory savedPointHistory = pointUseHistoryRepository.findById(pointHistory.getId()).orElse(null);
    assertNotNull(savedPointHistory);
    assertEquals(pointHistory.getAmount(), savedPointHistory.getAmount());
    assertEquals(user.getId(), savedPointHistory.getUser().getId());
  }
}