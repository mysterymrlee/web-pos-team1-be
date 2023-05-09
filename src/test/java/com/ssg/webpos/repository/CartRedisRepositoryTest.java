package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PhoneNumberRequestDTO;
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
    @DisplayName("posId로 해당 cart 정보 가져오기")
    public void readCartInfoFromRedisWithPosId () throws Exception {
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
    PhoneNumberRequestDTO phoneNumberRequestDto = new PhoneNumberRequestDTO();
    phoneNumberRequestDto.setPhoneNumber("01011112222");


      // when
    cartRedisRepository.save(cartAddDTO1, phoneNumberRequestDto);

    Map<String, List<Object>> byId = cartRedisRepository.findById(String.valueOf(cartAddDTO1.getPosId()));
    System.out.println("cartAddDTOList = " + byId);
    Assertions.assertEquals(2, byId.size());
    }

    @Test
    @DisplayName("포인트 redis 저장")
    public void readPointInfoFromRedisAll () throws Exception {
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
      cartAddDTO3.setProductId(3L);
      cartAddDTO3.setQty(4);

      PhoneNumberRequestDTO phoneNumberRequestDto = new PhoneNumberRequestDTO();
      phoneNumberRequestDto.setPhoneNumber("01011112222");


      // when
      cartRedisRepository.save(cartAddDTO1, phoneNumberRequestDto);
      cartRedisRepository.save(cartAddDTO2, phoneNumberRequestDto);
      cartRedisRepository.save(cartAddDTO3, phoneNumberRequestDto);

      // then
      Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
      System.out.println("All data: " + all);

      Map<String, List<Object>> posData1 = all.get(String.valueOf(cartAddDTO1.getPosId()));
      Assertions.assertNotNull(posData1);

      List<Object> cartList1 = posData1.get("cart");
      Assertions.assertNotNull(cartList1);
      CartAddDTO savedCart1 = (CartAddDTO) cartList1.get(0);
      Assertions.assertEquals(cartAddDTO1.getPosId(), savedCart1.getPosId());
      Assertions.assertEquals(cartAddDTO1.getProductId(), savedCart1.getProductId());
      Assertions.assertEquals(cartAddDTO1.getQty(), savedCart1.getQty());
    }

  @Test
  @DisplayName("해당 posId 삭제")
  public void deleteFromRedisWithPosId () throws Exception {
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
    cartAddDTO3.setProductId(3L);
    cartAddDTO3.setQty(4);

    PhoneNumberRequestDTO phoneNumberRequestDto = new PhoneNumberRequestDTO();
    phoneNumberRequestDto.setPhoneNumber("01011112222");

    cartRedisRepository.save(cartAddDTO1, phoneNumberRequestDto);
    cartRedisRepository.save(cartAddDTO2, phoneNumberRequestDto);
    cartRedisRepository.save(cartAddDTO3, phoneNumberRequestDto);

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
    CartAddDTO cartAddDTO1 = new CartAddDTO();
    cartAddDTO1.setPosId(1L);
    cartAddDTO1.setProductId(2L);
    cartAddDTO1.setQty(3);

    CartAddDTO updateDTO = new CartAddDTO();
    updateDTO.setPosId(1L);
    updateDTO.setProductId(5L);
    updateDTO.setQty(5);

    PhoneNumberRequestDTO phoneNumberRequestDto = new PhoneNumberRequestDTO();
    phoneNumberRequestDto.setPhoneNumber("01011112222");

    cartRedisRepository.save(cartAddDTO1, phoneNumberRequestDto);

    Map<String, Map<String, List<Object>>> allBeforeUpdate = cartRedisRepository.findAll();
    System.out.println("Before update: " + allBeforeUpdate);

    // when
    cartRedisRepository.update(updateDTO, phoneNumberRequestDto);

    //then
    Map<String, Map<String, List<Object>>> allAfterUpdate = cartRedisRepository.findAll();
    System.out.println("After update: " + allAfterUpdate);

    Map<String, List<Object>> posData = allAfterUpdate.get(String.valueOf(updateDTO.getPosId()));
    Assertions.assertNotNull(posData);

    List<Object> cartList = posData.get("cart");
    Assertions.assertNotNull(cartList);

    CartAddDTO updatedCart = (CartAddDTO) cartList.get(0);
    Assertions.assertEquals(updateDTO.getPosId(), updatedCart.getPosId());
    Assertions.assertEquals(updateDTO.getProductId(), updatedCart.getProductId());
    Assertions.assertEquals(updateDTO.getQty(), updatedCart.getQty());

  }

  @Test
  void deleteAll() throws Exception {
    cartRedisRepository.deleteAll();

    Map<String, Map<String, List<Object>>> allAfterDeletion = cartRedisRepository.findAll();
    System.out.println("After deletion: " + allAfterDeletion);

    Assertions.assertTrue(allAfterDeletion.isEmpty());
  }



  }

