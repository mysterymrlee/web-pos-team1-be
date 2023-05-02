package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CartRedisRepositoryTest {
  @Autowired
  CartRedisRepository cartRedisRepository;

  @Test
  void testSaveAndFindAll() throws Exception {
    // given
    CartDto cartDto = new CartDto();
    cartDto.setId("1113");
    cartDto.setTotalPrice(1000);
    cartDto.setFinalPrice(20000);

    // when
    cartRedisRepository.save(cartDto);
    try {
      Map<String, CartDto> cartMap = cartRedisRepository.findAll();
      System.out.println("cartMap = " + cartMap);
    } catch (Exception e) {
      System.out.println("e.getMessage() = " + e.getMessage());
      System.out.println("e.getStackTrace() = " + e.getStackTrace());
      System.out.println("CartRedisRepositoryTest.testSaveAndFindAll");
    }
  }

  @Test
  public void testUpdate() {
    // given
    CartDto cartDto = new CartDto();
    cartDto.setId("1");
    cartDto.setTotalPrice(1000);
    cartDto.setFinalPrice(20000);
    cartRedisRepository.save(cartDto);

    CartDto updatedCartDto = new CartDto();
    updatedCartDto.setId("1");
    updatedCartDto.setTotalPrice(5000);
    updatedCartDto.setFinalPrice(100000);

    // when
    cartRedisRepository.update(updatedCartDto);
    CartDto resultCartDto = cartRedisRepository.findById("1");
    System.out.println("resultCartDto = " + resultCartDto);

    // then
    assertThat(resultCartDto).isNotNull();
    assertThat(resultCartDto.getId()).isEqualTo("1");
    assertThat(resultCartDto.getTotalPrice()).isEqualTo(5000);
    assertThat(resultCartDto.getFinalPrice()).isEqualTo(100000);
  }
}