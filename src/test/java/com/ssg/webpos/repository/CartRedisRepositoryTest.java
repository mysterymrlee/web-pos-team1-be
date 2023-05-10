package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PhoneNumberDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

  @Autowired
  UserRepository userRepository;

    @Test
    @DisplayName("카트 redis 저장")
    public void readCartInfoFromRedisWithPosId () throws Exception {
    // given
    CartAddDTO cartAddDTO1 = new CartAddDTO();
    cartAddDTO1.setPosId(1L);
    cartAddDTO1.setProductId(5L);
    cartAddDTO1.setCartQty(5);

    
      // when
    cartRedisRepository.saveCart(cartAddDTO1);


    Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
    System.out.println("all = " + all);

    Map<String, List<Object>> byId = cartRedisRepository.findById(String.valueOf(cartAddDTO1.getPosId()));
    System.out.println("cartAddDTOList = " + byId);
    Assertions.assertEquals(1, byId.size());
    }

    @Test
    @DisplayName("포인트 redis 저장")
    public void readPointInfoFromRedisAll () throws Exception {
      
      PhoneNumberDTO phoneNumberDto = new PhoneNumberDTO();
      phoneNumberDto.setPhoneNumber("01011113333");
      phoneNumberDto.setPosId(1L);
      cartRedisRepository.savePoint(phoneNumberDto);

      Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
      Map<String, List<Object>> byId = cartRedisRepository.findById(String.valueOf(phoneNumberDto.getPosId()));
      Assertions.assertEquals(1, byId.size());


    }

  @Test
  @DisplayName("해당 posId 삭제")
  public void deleteFromRedisWithPosId () throws Exception {
    // given
    CartAddDTO cartAddDTO1 = new CartAddDTO();
    cartAddDTO1.setPosId(1L);
    cartAddDTO1.setProductId(2L);
    cartAddDTO1.setCartQty(3);

    CartAddDTO cartAddDTO2 = new CartAddDTO();
    cartAddDTO2.setPosId(1L);
    cartAddDTO2.setProductId(3L);
    cartAddDTO2.setCartQty(4);

    CartAddDTO cartAddDTO3 = new CartAddDTO();
    cartAddDTO3.setPosId(2L);
    cartAddDTO3.setProductId(3L);
    cartAddDTO3.setCartQty(4);

    PhoneNumberDTO phoneNumberDto = new PhoneNumberDTO();
    phoneNumberDto.setPhoneNumber("01011112222");


    // when
    cartRedisRepository.delete(String.valueOf(cartAddDTO1.getPosId()));
    Map<String, Map<String, List<Object>>> allAfterDeletion = cartRedisRepository.findAll();
    System.out.println("allAfterDeletion = " + allAfterDeletion);

    //then
    Assertions.assertFalse(allAfterDeletion.containsKey(String.valueOf(cartAddDTO1.getPosId())));
  }
  @Test
  @DisplayName("redis에 저장된 CartAddDTO 업데이트")
  public void updateFromRedisWithPosId () throws Exception {
    // given
    PhoneNumberDTO beforeDTO = new PhoneNumberDTO();
    beforeDTO.setPhoneNumber("01011112222");
    
    PhoneNumberDTO updateDTO = new PhoneNumberDTO();

    cartRedisRepository.savePoint(beforeDTO);

    Map<String, Map<String, List<Object>>> allBeforeUpdate = cartRedisRepository.findAll();
    System.out.println("Before update: " + allBeforeUpdate);

    // when
    cartRedisRepository.updatePoint(updateDTO);

    //then
    Map<String, Map<String, List<Object>>> allAfterUpdate = cartRedisRepository.findAll();
    System.out.println("After update: " + allAfterUpdate);

    Map<String, List<Object>> posData = allAfterUpdate.get(String.valueOf(updateDTO.getPosId()));
    System.out.println("posData = " + posData);

  }

  @Test
  void deleteAll() throws Exception {
    cartRedisRepository.deleteAll();

    Map<String, Map<String, List<Object>>> allAfterDeletion = cartRedisRepository.findAll();
    System.out.println("After deletion: " + allAfterDeletion);

    Assertions.assertTrue(allAfterDeletion.isEmpty());
  }



  }

