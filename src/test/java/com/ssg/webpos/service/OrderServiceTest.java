package com.ssg.webpos.service;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.Pos;
import com.ssg.webpos.domain.Product;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public class OrderServiceTest {
  @Autowired OrderRepository orderRepository;
  @Autowired ProductRepository productRepository;
  @Autowired
  CartRepository orderProductRepository;
  @Autowired
  CartService orderService;
  @Autowired StoreRepository storeRepository;
  @Autowired PosRepository posRepository;

  @Test
  void addOrder() throws Exception {
    Product findProduct = productRepository.findById(1L).get();
    Pos findPos = posRepository.findById(1L).get();
    Order findOrder = orderRepository.findOrderById(1L);
//    if (findProduct1.isEmpty()) {
//      throw new Exception();
//    }
    // store 생성 -> 했음
    // storeRepository.findById(1L);
    // pos 생성 -> 했음
    // posRepository.findById(1L);
    // product 찾기
//    OrderProduct op = new OrderProduct();
//    orderProductRepository.save(op);
    Cart orderProduct = new Cart();

    orderProduct.setQty(2);
    orderProduct.setProduct(findProduct);
    orderProduct.setOrder(findOrder);

    orderService.addCart(findPos, findProduct, 3);

    System.out.println(findOrder);
//    orderProduct.setProduct(findProduct);

  }
}