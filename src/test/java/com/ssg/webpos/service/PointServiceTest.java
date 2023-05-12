package com.ssg.webpos.service;

import com.ssg.webpos.domain.User;
import com.ssg.webpos.domain.enums.RoleUser;
import com.ssg.webpos.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class PointServiceTest {

  @Autowired
  private PointService pointService;

  @Autowired
  private UserRepository userRepository;

  @Test
   void UpdatePoint() {
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

    pointService.updatePoint(phoneNumber, finalPrice);

    User updatedUser = userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    assertNotNull(updatedUser);
    assertEquals(105, updatedUser.getPoint());
  }

}