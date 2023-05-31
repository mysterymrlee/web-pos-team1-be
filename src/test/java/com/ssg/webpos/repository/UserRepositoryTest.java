package com.ssg.webpos.repository;

import com.ssg.webpos.domain.User;
import com.ssg.webpos.domain.enums.RoleUser;
import com.ssg.webpos.dto.point.PointDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@Transactional
class UserRepositoryTest {
  @Autowired
  UserRepository userRepository;

  @Test
  void 전화번호일치() {
    User user = new User();
    user.setName("고경환");
    user.setEmail("1111@naver.com");
    user.setPhoneNumber("01033334444");
    user.setPassword("1234");
    user.setRole(RoleUser.NORMAL);
    int currentPoint = 500;
    user.setPoint(currentPoint);
    userRepository.save(user);

    PointDTO pointDTO = new PointDTO();
    pointDTO.setPhoneNumber("01033334444");

    // when
    Optional<User> findUser = userRepository.findByPhoneNumber(pointDTO.getPhoneNumber());
    System.out.println("findUser = " + findUser);

    // then
    assertTrue(findUser.isPresent());
    assertThat(findUser.get().getName()).isEqualTo("고경환"); // 회원 이름이 일치하는지 확인
  }

  @Test
  void 전화번호불일치() {
    //given
    User user = new User();
    user.setName("홍길동1");
    user.setEmail("1111@naver.com");
    user.setPhoneNumber("01033334444");
    user.setPassword("1234");
    user.setRole(RoleUser.NORMAL);
    int currentPoint = 500;
    user.setPoint(currentPoint);
    userRepository.save(user);

    PointDTO pointDTO = new PointDTO();
    pointDTO.setPhoneNumber("01011112226");

    // when
    Optional<User> findUser = userRepository.findByPhoneNumber(pointDTO.getPhoneNumber());
    System.out.println("findUser = " + findUser);

    // then
    assertTrue(findUser.isEmpty());// findUser가 비어있어야함
  }
}