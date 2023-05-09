//package com.ssg.webpos.service;
//
//import com.ssg.webpos.domain.Cart;
//import com.ssg.webpos.domain.Order;
//import com.ssg.webpos.domain.PosId;
//import com.ssg.webpos.domain.Product;
//import com.ssg.webpos.dto.CartAddDTO;
//import com.ssg.webpos.repository.cart.CartRepository;
//import com.ssg.webpos.repository.order.OrderRepository;
//import com.ssg.webpos.repository.pos.PosRepository;
//import com.ssg.webpos.repository.product.ProductRepository;
//import com.ssg.webpos.repository.store.StoreRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//
//import javax.transaction.Transactional;
//import java.util.List;
//import java.util.Optional;
//
//@SpringBootTest
//@Transactional
//public class CartServiceTest {
//  @Autowired
//  OrderRepository orderRepository;
//  @Autowired
//  ProductRepository productRepository;
//  @Autowired
//  CartRepository cartRepository;
//  @Autowired
//  CartService cartService;
//  @Autowired
//  StoreRepository storeRepository;
//  @Autowired
//  PosRepository posRepository;
////  @Autowired
////  RedisTemplate<String, Object> redisTemplate;
//
//
//  @Test
//  void addCart() throws Exception {
//    Long productId = productRepository.findById(10L).get().getId();
//    PosId posId = new PosId();
//    posId.setPos_id(1L);
//    posId.setStore_id(1L);
//    PosId posId1 = posRepository.findById(posId).get().getId();
//    CartAddDTO cartAddDTO = new CartAddDTO(posId1, productId, 1);
//    cartService.addCart(cartAddDTO);
//
//    Product findProduct = productRepository.findById(cartAddDTO.getProductId()).get();
//    List<Order> findOrderList = orderRepository.findAll();
//    for (Order order : findOrderList) {
//      System.out.println("order = " + order.getTotalPrice());
//    }
//    Assertions.assertEquals(findProduct.getSalePrice() * 2, findOrderList.get(0).getTotalPrice());
//  }
//
//  @Test
//  @DisplayName("장바구니 삭제 후 주문테이블에서 totalPrice 반영테스트")
//  void delCart() {
//    Long orderId = 25L;
//    Long cartId = 29L;
//    Order beforeOrder = orderRepository.findById(orderId).get();
//    int beforeTotalPrice = beforeOrder.getTotalPrice();
//    Cart findCart = cartRepository.findById(cartId).get();
//    Product findProduct = findCart.getProduct();
//    int diffPrice = findCart.getQty() * findProduct.getSalePrice();
//
//    cartService.delCart(cartId);
//
//    Order afterOrder = orderRepository.findById(orderId).get();
//
//    Assertions.assertEquals(beforeTotalPrice - diffPrice, afterOrder.getTotalPrice());
//  }
//}