package com.ssg.webpos.service;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartRedisService {
  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final PosRepository posRepository;

  private final CartRedisRepository cartRedisRepository;

  @Transactional
  public void saveCartToDB(String compositeId) {
    Map<String, List<Object>> posData = cartRedisRepository.findById(compositeId);
    System.out.println("posData = " + posData);
    if (posData != null) {
      List<Object> cartList = posData.get("cartList");
      if (cartList != null && !cartList.isEmpty()) {
        for (Object obj : cartList) {
          Map<String, Object> cartItem = (Map<String, Object>) obj;
          Long productId = (Long) cartItem.get("productId");
          int cartQty = (int) cartItem.get("cartQty");

          Cart cart = new Cart();

          Product product = productRepository.findById(productId)
              .orElseThrow(() -> new RuntimeException("product 찾을 수 없습니다."));
          System.out.println("product = " + product);


          cart.setProduct(product);
          cart.setQty(cartQty);
          cartRepository.save(cart);
        }
      }
    }
  }
}


