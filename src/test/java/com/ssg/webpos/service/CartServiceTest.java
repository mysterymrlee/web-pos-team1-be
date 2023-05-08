package com.ssg.webpos.service;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.OrderDTO;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CartServiceTest {
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  CartRepository cartRepository;
  @Autowired
  CartService cartService;
  @Autowired
  StoreRepository storeRepository;
  @Autowired
  PosRepository posRepository;
//  @Autowired
//  RedisTemplate<String, Object> redisTemplate;

  @Test
  void addCart() throws Exception {
    Long productId = productRepository.findById(10L).get().getId();
    Long posId = posRepository.findById(1L).get().getId();
    CartAddDTO cartAddDTO = new CartAddDTO(posId, productId, 1);
    cartService.addCart(cartAddDTO);

    Product findProduct = productRepository.findById(cartAddDTO.getProductId()).get();
    List<Order> findOrderList = orderRepository.findAll();
    for (Order order : findOrderList) {
      System.out.println("order = " + order.getTotalPrice());
    }
    assertEquals(findProduct.getSalePrice() * 2, findOrderList.get(0).getTotalPrice());
  }

  @Test
  @DisplayName("장바구니 삭제 후 주문테이블에서 totalPrice 반영테스트")
  void delCart() {
    Long orderId = 25L;
    Long cartId = 29L;
    Order beforeOrder = orderRepository.findById(orderId).get();
    int beforeTotalPrice = beforeOrder.getTotalPrice();
    Cart findCart = cartRepository.findById(cartId).get();
    Product findProduct = findCart.getProduct();
    int diffPrice = findCart.getQty() * findProduct.getSalePrice();

    cartService.delCart(cartId);

    Order afterOrder = orderRepository.findById(orderId).get();

    assertEquals(beforeTotalPrice - diffPrice, afterOrder.getTotalPrice());
  }
  @Test
  void addOrder() {
    Long posId = 1L;
    Long productId1 = 6L;
    Long productId2 = 6L;
    Long productId3 = 9L;
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
      price += cartAddDTO.getQty() * product.getSalePrice();
      qty += cartAddDTO.getQty();
    }
    orderDTO.setTotalQuantity(qty);
    orderDTO.setFinalTotalPrice(price);

    // when
    Order savedOrder = cartService.addOrder(cartAddDTOList, cartAddDTO1, orderDTO);

    // then
    System.out.println("savedOrder = " + savedOrder);
    List<Cart> savedCarts = savedOrder.getCartList();
    System.out.println("savedCarts = " + savedCarts);
    assertEquals(4, savedOrder.getTotalQuantity());
    assertEquals(3, savedCarts.size());
  }
}