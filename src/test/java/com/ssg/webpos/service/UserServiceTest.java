package com.ssg.webpos.service;

import com.ssg.webpos.domain.Point;
import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.domain.enums.RoleUser;
import com.ssg.webpos.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class UserServiceTest {
  @Autowired
  UserRepository userRepository;
  @Autowired
  UserService userService;

  @Test
  void checkMemberExistByPhoneNumber() {
    // 기존 사용자가 존재하는 경우
    User user = new User();
    user.setName("고경환");
    user.setEmail("1111@naver.com");
    user.setPhoneNumber("01033334444");
    user.setPassword("1234");
    user.setRole(RoleUser.NORMAL);
    userRepository.save(user);
    String phoneNumber = user.getPhoneNumber();
    System.out.println("phoneNumber = " + phoneNumber);
    

    boolean result = userService.checkMemberExistByPhoneNumber(phoneNumber);
    Assertions.assertTrue(result);
    
  }

  @Test
  void checkMemberExistByUserId() {
    // 기존 사용자가 존재하는 경우
    User user = new User();
    user.setName("고경환");
    user.setEmail("1111@naver.com");
    user.setPhoneNumber("01033334444");
    user.setPassword("1234");
    user.setRole(RoleUser.NORMAL);
    userRepository.save(user);
    String phoneNumber = user.getPhoneNumber();
    System.out.println("phoneNumber = " + phoneNumber);


    boolean result = userService.checkMemberExistByUserId(user.getId());
    Assertions.assertTrue(result);
  }
    @Test
  void login() {
      User user = new User();
      user.setName("홍길동");
      user.setEmail("1111@naver.com");
      user.setPhoneNumber("01099999999");
      user.setPassword("1234");
      user.setRole(RoleUser.NORMAL);
      userRepository.save(user);

      PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
      posStoreCompositeId.setPos_id(2L);
      posStoreCompositeId.setStore_id(2L);

      userService.login(user, posStoreCompositeId);

    }
}