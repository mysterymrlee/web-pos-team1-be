package com.ssg.webpos.repository;

import com.ssg.webpos.domain.User;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.time.LocalDate;

import static com.ssg.webpos.domain.enums.RoleUser.NORMAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@Transactional
class UserRepositoryTest {
  @Autowired
  UserRepository userRepository;

  @BeforeEach
  void 사용자만들기() {
    User user = new User();
    user.setName("유하경");
    user.setEmail("yhk313");
    user.setPassword("1111");
    user.setRole(NORMAL);
    user.setPhoneNumber("01011112225");
    userRepository.save(user);
  }

  @Transactional
  @Test
  void 전화번호일치() {
    String phoneNumber = "01011112225";

    // when
    Optional<User> findUser = userRepository.findByPhoneNumber(phoneNumber);
    System.out.println("findUser = " + findUser);

    // then
    assertTrue(findUser.isPresent());
    assertThat(findUser.get().getName()).isEqualTo("유하경"); // 회원 이름이 일치하는지 확인
  }

  @Test
  void 전화번호불일치() {
    //given
    String phoneNumber = "01011112226";
    // when
    Optional<User> findUser = userRepository.findByPhoneNumber(phoneNumber);
    System.out.println("findUser = " + findUser);

    // then
    assertTrue(findUser.isEmpty());// findUser가 비어있어야함
  }

  @Test
  void 전화번호_자릿수_테스트() {
    //given
    String phoneNumber = "0101111";
    //when
    Optional<User> findUser = userRepository.findByPhoneNumber(phoneNumber);

    //then
    assertTrue(findUser.isEmpty());
  }

  }