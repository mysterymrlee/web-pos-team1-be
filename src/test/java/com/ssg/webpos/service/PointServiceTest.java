package com.ssg.webpos.service;

import com.ssg.webpos.domain.User;
import com.ssg.webpos.domain.enums.RoleUser;
import com.ssg.webpos.dto.PointDTO;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

  @Test
  @DisplayName("포인트 업데이트 테스트")
  void updatePoint() {
    String phoneNumber = "01012345678";
    User user = new User();
    user.setName("홍길동");
    user.setEmail("1111@naver.com");
    user.setPhoneNumber(phoneNumber);
    user.setPassword("1234");
    user.setRole(RoleUser.NORMAL);
    user.setPoint(100);
    userRepository.save(user);


    int finalPrice = 5000;

    pointService.updatePoint(user.getId(), finalPrice);

    User updatedUser = userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    assertNotNull(updatedUser);
    assertEquals(105, updatedUser.getPoint());
  }


  @Test
  @DisplayName("user 포인트 차감 테스트")
  void deductPointsFromUser() throws Exception {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPoint(2000);
    user.setName("하경");
    user.setRole(RoleUser.NORMAL);
    user.setPassword("1111");
    user.setEmail("yhk@naver.com");
    user.setPhoneNumber("01011111111");
    userRepository.save(user);

    PointDTO pointDTO = new PointDTO();
    pointDTO.setPhoneNumber(user.getPhoneNumber());
    pointDTO.setPointMethod("phoneNumber");
    pointDTO.setStoreId(1L);
    pointDTO.setPosId(1L);
    cartRedisRepository.savePoint(pointDTO);

    Long userId = cartRedisRepository.findUserId(pointDTO.getStoreId() + "-" + pointDTO.getPosId());
    int deductedAmount = 50;

    // When
    pointService.deductPoints(userId, deductedAmount);

    // Then
    User updatedUser = userRepository.findById(userId).get();
    assertEquals(user.getPoint() - deductedAmount, updatedUser.getPoint());
  }
  @Test
  @DisplayName("포인트 차감 실패: 포인트 부족")
  void deductPointsNotEnoughPointsExceptionThrown() {
    // Given
    Long userId = 1L;
    int deductedAmount = 100;

    User user = new User();
    user.setId(userId);
    user.setPoint(50);
    userRepository.save(user);

    try {
      pointService.deductPoints(userId, deductedAmount);
    } catch (RuntimeException e) {
      assertEquals("포인트가 부족합니다.", e.getMessage());
    }

  }

}