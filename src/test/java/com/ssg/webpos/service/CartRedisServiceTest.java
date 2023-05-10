package com.ssg.webpos.service;

import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Product;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.OrderDTO;
import com.ssg.webpos.dto.PhoneNumberDTO;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
    phoneNumberDTO.setPhoneNumber("01055555555");

    //when
    cartRedisService.addCart(cartAddDTO);

    cartRedisRepository.findAll();


  }

  @Test
  void addOrder() {
    Long posId = 1L;
    Long productId1 = 1L;
    Long productId2 = 2L;
    Long productId3 = 3L;
    List<CartAddDTO> cartAddDTOList = new ArrayList<>();
    // given
    CartAddDTO cartAddDTO1 = new CartAddDTO(posId, productId1, 1);
    CartAddDTO cartAddDTO2 = new CartAddDTO(posId, productId2, 1);
    CartAddDTO cartAddDTO3 = new CartAddDTO(posId, productId3, 2);

    cartAddDTOList.add(cartAddDTO1);
    cartAddDTOList.add(cartAddDTO2);
    cartAddDTOList.add(cartAddDTO3);
    System.out.println("cartAddDTOList = " + cartAddDTOList);
    OrderDTO orderDTO = new OrderDTO();

    int price = 0;
    int qty = 0;
    for (CartAddDTO cartAddDTO : cartAddDTOList) {
      Product product = productRepository.findById(cartAddDTO.getProductId()).get();
      price += cartAddDTO.getCartQty() * product.getSalePrice();
      qty += cartAddDTO.getCartQty();
    }
    orderDTO.setTotalQuantity(qty);
    orderDTO.setFinalTotalPrice(price);

    // when
    Order savedOrder = cartRedisService.addOrder(cartAddDTOList, cartAddDTO1, orderDTO);

    // then
    System.out.println("savedOrder = " + savedOrder);
    List<Cart> savedCarts = savedOrder.getCartList();
    System.out.println("savedCarts = " + savedCarts);
    assertEquals(4, savedOrder.getTotalQuantity());
    assertEquals(3, savedCarts.size());
  }
}
