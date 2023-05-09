package com.ssg.webpos.service;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PhoneNumberRequestDTO;
import com.ssg.webpos.repository.CartRedisImplRepository;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Rollback(value = false)
class CartRedisServiceTest {

  @Autowired
  OrderRepository orderRepository;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  CartRepository cartRepository;

  @Autowired
  StoreRepository storeRepository;
  @Autowired
  PosRepository posRepository;
  @Autowired
  CartService cartService;

  @Autowired
  CartRedisService cartRedisService;

  @Autowired
  CartRedisImplRepository cartRedisRepository;


  @Test
  void addCart() throws Exception {
    //given
    Long productId = productRepository.findById(1L).get().getId();
    Long posId = posRepository.findById(1L).get().getId();
    CartAddDTO cartAddDTO = new CartAddDTO(posId, productId, 2);
    PhoneNumberRequestDTO phoneNumberRequestDTO = new PhoneNumberRequestDTO();
    phoneNumberRequestDTO.setPhoneNumber("01055555555");

    //when
    cartRedisService.addCart(cartAddDTO, phoneNumberRequestDTO);

    cartRedisRepository.findAll();


  }

}
