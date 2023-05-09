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

  @Test
  void addCartTest() throws Exception {
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
  void delCartTest() {
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
  void addOrderTest() {
    Long posId = 1L;
    Long productId1 = 3L;
    Long productId2 = 6L;
    Long productId3 = 9L;
    List<CartAddDTO> cartAddDTOList = new ArrayList<>();
    // given
    CartAddDTO cartAddDTO1 = new CartAddDTO(posId, productId1, 5);
    CartAddDTO cartAddDTO2 = new CartAddDTO(posId, productId2, 5);
    CartAddDTO cartAddDTO3 = new CartAddDTO(posId, productId3, 10);

    cartAddDTOList.add(cartAddDTO1);
    cartAddDTOList.add(cartAddDTO2);
    cartAddDTOList.add(cartAddDTO3);
    System.out.println("cartAddDTOList = " + cartAddDTOList);
    OrderDTO orderDTO = new OrderDTO();

    int price = 0, qty = 0;
    for (CartAddDTO cartAddDTO : cartAddDTOList) {
      Product product = productRepository.findById(cartAddDTO.getProductId()).get();
      price += cartAddDTO.getQty() * product.getSalePrice();
      qty += cartAddDTO.getQty();
    }
    orderDTO.setTotalQuantity(qty);
    orderDTO.setFinalTotalPrice(price);

    // when
    Product product1 = productRepository.findById(productId1).get();
    int beforeStock1 = product1.getStock();
    System.out.println("beforeStock1 = " + beforeStock1);

    Order savedOrder = cartService.addOrder(cartAddDTOList, cartAddDTO1, orderDTO);
    System.out.println("savedOrder = " + savedOrder);

    int afterStock1 = product1.getStock();
    System.out.println("afterStock1 = " + afterStock1);

    // then
    List<Cart> savedCarts = savedOrder.getCartList();
    for(Cart cart : savedCarts) {
      System.out.println("cart = " + cart);
    }
    System.out.println("savedCarts = " + savedCarts);
    int orderQty = savedCarts.get(0).getQty();
    assertEquals(20, savedOrder.getTotalQuantity());
    assertEquals(3, savedCarts.size());
    assertEquals(beforeStock1 - orderQty, afterStock1);
  }

  @Test
  void cancelOrderTest() {
    // beforeStock : 70, orderQty : 5, afterStock : 65
    Long orderId = addOrder();
    Order findOrder = orderRepository.findById(orderId).get();
    System.out.println("findOrder = " + findOrder);
    // 재고 수량 저장을 위한 리스트
    List<Product> productList = new ArrayList<>();
    List<Product> allProductList = productRepository.findAll();
    List<Cart> cartList = findOrder.getCartList();

    for (Cart cart : cartList) {
      Product product = cart.getProduct();
      System.out.println("product = " + product);
      productList.add(product);
    }
    System.out.println("productList = " + productList);

    Order cancelOrder = orderRepository.findById(orderId).get();

    // 주문 취소로 인한 재고 수량 증가 확인
    for (Product cartProduct : productList) {
      int index = allProductList.indexOf(cartProduct);
      Product findProduct = allProductList.get(index);
      int beforeStock = findProduct.getStock();
      System.out.println("beforeStock = " + beforeStock);
      cartService.cancelOrder(orderId);
      int canceledQty = (int) findOrder.getCartList().stream()
          .filter(cart -> cart.getProduct().equals(cartProduct))
          .mapToLong(Cart::getQty)
          .sum();
      System.out.println("canceledQty = " + canceledQty);

      int expectedStock = beforeStock + canceledQty;
      int actualStock = productRepository.findById(cartProduct.getId()).get().getStock();
      System.out.println("expectedStock = " + expectedStock);
      System.out.println("actualStock = " + actualStock);

      assertEquals(OrderStatus.CANCEL, cancelOrder.getOrderStatus());
      assertEquals(expectedStock, actualStock);
    }
  }

  Long addOrder() {
    Long posId = 1L;
    Long productId1 = 3L;
    Long productId2 = 6L;
    Long productId3 = 9L;
    List<CartAddDTO> cartAddDTOList = new ArrayList<>();
    // given
    CartAddDTO cartAddDTO1 = new CartAddDTO(posId, productId1, 5);
    CartAddDTO cartAddDTO2 = new CartAddDTO(posId, productId2, 5);
    CartAddDTO cartAddDTO3 = new CartAddDTO(posId, productId3, 10);

    cartAddDTOList.add(cartAddDTO1);
    cartAddDTOList.add(cartAddDTO2);
    cartAddDTOList.add(cartAddDTO3);
    System.out.println("cartAddDTOList = " + cartAddDTOList);
    OrderDTO orderDTO = new OrderDTO();

    int price = 0, qty = 0;
    for (CartAddDTO cartAddDTO : cartAddDTOList) {
      Product product = productRepository.findById(cartAddDTO.getProductId()).get();
      price += cartAddDTO.getQty() * product.getSalePrice();
      qty += cartAddDTO.getQty();
    }
    orderDTO.setTotalQuantity(qty);
    orderDTO.setFinalTotalPrice(price);

    // when
    Product product1 = productRepository.findById(productId1).get();
    int beforeStock1 = product1.getStock();
    System.out.println("beforeStock1 = " + beforeStock1);

    Order savedOrder = cartService.addOrder(cartAddDTOList, cartAddDTO1, orderDTO);
    System.out.println("savedOrder = " + savedOrder);

    int afterStock1 = product1.getStock();
    System.out.println("afterStock1 = " + afterStock1);

    // then
    List<Cart> savedCarts = savedOrder.getCartList();
    for(Cart cart : savedCarts) {
      System.out.println("cart = " + cart);
    }
    System.out.println("savedCarts = " + savedCarts);
    int orderQty = savedCarts.get(0).getQty();
    assertEquals(20, savedOrder.getTotalQuantity());
    assertEquals(3, savedCarts.size());
    assertEquals(beforeStock1 - orderQty, afterStock1);
    return savedOrder.getId();
  }
}