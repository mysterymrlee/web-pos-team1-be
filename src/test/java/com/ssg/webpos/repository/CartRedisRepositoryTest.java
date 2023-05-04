package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartAddDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class CartRedisRepositoryTest {
  @Autowired
  CartRedisImplRepository cartRedisRepository;

  @Test
  void testSaveAndFindAll() throws Exception {
    // given
    CartAddDTO cartAddDTO = new CartAddDTO();
    cartAddDTO.setPosId(1L);
    cartAddDTO.setProductId(2L);
    cartAddDTO.setQty(3);


    // when
    cartRedisRepository.save(cartAddDTO);
    try {
      Map<String, List<CartAddDTO>> cartMap = cartRedisRepository.findAll();
      System.out.println("cartMap = " + cartMap);
    } catch (Exception e) {
      System.out.println("e.getMessage() = " + e.getMessage());
      System.out.println("e.getStackTrace() = " + e.getStackTrace());
      System.out.println("CartRedisRepositoryTest.testSaveAndFindAll");
    }
  }

  @Test
  public void testUpdate() {
//    // given
//    CartDto cartDto = new CartDto();
//    cartDto.setPosId(1L);
//    cartDto.setProductId(2L);
//    cartDto.setQty(3);
//    cartRedisRepository.save(cartDto);
//    System.out.println("cartDto = " + cartDto);
//
//    CartDto updatedCartDto = new CartDto();
//    cartDto.setPosId(1L);
//    cartDto.setProductId(2L);
//    cartDto.setQty(3)
//
//    // when
//    cartRedisRepository.update(updatedCartDto);
//    CartDto resultCartDto = cartRedisRepository.findById("1");
//    System.out.println("resultCartDto = " + resultCartDto);
//
//    // then
//    assertThat(resultCartDto).isNotNull();
//    assertThat(resultCartDto.getId()).isEqualTo("1");
//    assertThat(resultCartDto.getTotalPrice()).isEqualTo(5000);
//    assertThat(resultCartDto.getFinalPrice()).isEqualTo(100000);
  }
  @Test
  public void delete() {
//    CartDto cartDto = new CartDto();
//    cartDto.setId("1");
//    cartDto.setTotalPrice(1000);
//    cartDto.setFinalPrice(20000);
//    cartRedisRepository.save(cartDto);
//    System.out.println("cartDto = " + cartDto);
//
//    // when
//    cartRedisRepository.delete("1");
//    CartDto resultCartDto = cartRedisRepository.findById("1");
//    System.out.println("resultCartDto = " + resultCartDto);
//
//    // then
//    assertThat(resultCartDto).isNull();
  }

  @Test
  public void readCartInfoFromRedisAll() throws Exception {
//    // given
//    CartAddDTO cartAddDTO1 = new CartAddDTO();
//    cartAddDTO1.setPosId(1L);
//    cartAddDTO1.setProductId(2L);
//    cartAddDTO1.setQty(3);
//
//    CartAddDTO cartAddDTO2 = new CartAddDTO();
//    cartAddDTO2.setPosId(1L);
//    cartAddDTO2.setProductId(3L);
//    cartAddDTO2.setQty(4);
//
//    CartAddDTO cartAddDTO3 = new CartAddDTO();
//    cartAddDTO3.setPosId(2L);
//    cartAddDTO3.setProductId(2L);
//    cartAddDTO3.setQty(3);
//
//
//    // when
//    cartRedisRepository.save(cartAddDTO1);
//    cartRedisRepository.save(cartAddDTO2);
//    cartRedisRepository.save(cartAddDTO3);

    Map<String, List<CartAddDTO>> all = cartRedisRepository.findAll();
    System.out.println("all = " + all);
  }
  @Test
  public void readCartInfoFromRedisWithPosId() throws Exception {
    // given
    CartAddDTO cartAddDTO1 = new CartAddDTO();
    cartAddDTO1.setPosId(1L);
    cartAddDTO1.setProductId(2L);
    cartAddDTO1.setQty(3);

    CartAddDTO cartAddDTO2 = new CartAddDTO();
    cartAddDTO2.setPosId(1L);
    cartAddDTO2.setProductId(3L);
    cartAddDTO2.setQty(4);

    CartAddDTO cartAddDTO3 = new CartAddDTO();
    cartAddDTO3.setPosId(2L);
    cartAddDTO3.setProductId(2L);
    cartAddDTO3.setQty(3);


    // when
    cartRedisRepository.save(cartAddDTO1);
    cartRedisRepository.save(cartAddDTO2);
    cartRedisRepository.save(cartAddDTO3);

    List<CartAddDTO> cartAddDTOList = cartRedisRepository.findById(String.valueOf(cartAddDTO1.getPosId()));
    System.out.println("cartAddDTOList = " + cartAddDTOList);
    Assertions.assertEquals(2, cartAddDTOList.size());
  }
}
