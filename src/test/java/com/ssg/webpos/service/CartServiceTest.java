package com.ssg.webpos.service;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.Pos;
import com.ssg.webpos.domain.Product;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
public class CartServiceTest {
  @Autowired OrderRepository orderRepository;
  @Autowired ProductRepository productRepository;
  @Autowired
  CartRepository cartRepository;
  @Autowired
  CartService cartService;
  @Autowired StoreRepository storeRepository;
  @Autowired PosRepository posRepository;

  @Test
  void addCart() throws Exception {
    Long productId = productRepository.findById(2L).get().getId();
    Long posId = posRepository.findById(2L).get().getId();
    CartAddDTO cartAddDTO = new CartAddDTO(posId, productId, 2);
    cartService.addCart(cartAddDTO);

    Product findProduct = productRepository.findById(cartAddDTO.getProductId()).get();
    List<Order> findOrderList = orderRepository.findAll();
    for (Order order : findOrderList) {
      System.out.println("order = " + order.getTotalPrice());
    }
    Assertions.assertEquals(findProduct.getSalePrice() * 2, findOrderList.get(0).getTotalPrice());
  }
}