package com.ssg.webpos.service;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.Pos;
import com.ssg.webpos.domain.Product;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {
  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;

  // pos 당 order 하나 생성
  public void createCart(Pos pos) {
    Order order = Order.createOrder(pos);
    orderRepository.save(order);
  }

  // 상품 주문하기
  @Transactional
  public void addCart(Pos pos, Product newProduct, int qty) {
    // pos id로 해당 pos의 order 찾기
    Order order = orderRepository.findByPosId(pos.getId());

    // order가 존재하지 않는다면
    if(order == null) {
      order = Order.createOrder(pos);
      orderRepository.save(order);
    }
    Product product = productRepository.findById(newProduct.getId()).get();
    Cart cart = cartRepository.findByOrderIdAndProductId(order.getId(), product.getId());

    // order에 상품이 존재하지 않는다면 orderProduct 생성 후 추가
    if(cart == null) {
      cart = Cart.createOrderProduct(order, product, qty);
      cartRepository.save(cart);
    } else {
      // 상품이 order에 이미 존재한다면 수량만 증가
      Cart cart1 = new Cart(cart.getProduct(), cart.getOrder());
      cart1.addQty(qty);
      cartRepository.save(cart1);
    }
  }
}