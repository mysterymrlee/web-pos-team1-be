package com.ssg.webpos.service;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.Pos;
import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.OrderDTO;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final PosRepository posRepository;

  // 장바구니 담기 - Redis에 저장
  @Transactional
  public void addCart(CartAddDTO cartAddDTO) {
    // pos id로 해당 pos의 order 찾기
    Order order = orderRepository.findByPosId(cartAddDTO.getPosId());
    Pos pos = posRepository.findById(cartAddDTO.getPosId()).get();
    // order가 존재하지 않는다면
    if (order == null) {
      order = Order.createOrder(pos);
      order.setOrderStatus(OrderStatus.SUCCESS);
      order.setPayMethod(PayMethod.CREDIT_CARD);
      order.setTotalPrice(0);
      orderRepository.save(order);
    }
    Product product = productRepository.findById(cartAddDTO.getProductId()).get();
    order.changeTotalPrice(product.getSalePrice() * cartAddDTO.getQty());
    Cart cart = cartRepository.findByOrderIdAndProductId(order.getId(), product.getId());

    // order에 상품이 존재하지 않는다면 orderProduct 생성 후 추가
    if (cart == null) {
      cart = Cart.createOrderProduct(order, product, cartAddDTO.getQty());
    } else {
      // 상품이 order에 이미 존재한다면 수량만 증가
      cart.addQty(cartAddDTO.getQty());
    }
    cartRepository.save(cart);
  }
  // 장바구니 상품 개별 삭제
  @Transactional
  public void delCart(Long cartId) {
    Cart findCart = cartRepository.findById(cartId).get();
    Order findOrder = orderRepository.findById(findCart.getOrder().getId()).get();
    Product findProduct = findCart.getProduct();
    findOrder.minusTotalPrice(findCart.getQty() * findProduct.getSalePrice());

    cartRepository.deleteById(cartId);
  }

  // 주문 생성 후 장바구니 상품들 주문에 추가 - DB에 저장
  @Transactional
  public Order addOrder(List<CartAddDTO> cartAddDTOList, CartAddDTO cartAddDTO, OrderDTO orderDTO) {
    Pos pos = posRepository.findById(cartAddDTO.getPosId()).get();
    // 주문 생성
    Order order = new Order();
    System.out.println("order = " + order);
    order.setOrderStatus(OrderStatus.SUCCESS);
    order.setPayMethod(PayMethod.CREDIT_CARD);
    order.setTotalQuantity(orderDTO.getTotalQuantity());
    order.setPos(pos);
    List<Cart> cartList = order.getCartList();

    for(CartAddDTO cDTO : cartAddDTOList) {
      Product product = productRepository.findById(cDTO.getProductId()).get();
      Cart cart = new Cart(product, order);
      cart.setQty(cDTO.getQty());
      cartList.add(cart);
      cartRepository.save(cart);
    }
    orderRepository.save(order);

//    for (CartAddDTO cDTO : cartAddDTOList) {
//      Product product = productRepository.findById(cDTO.getProductId()).get();
//      // 이미 담겨있는 상품인 경우 수량만 더해줌
//      Cart existCart = cartRepository.findByProductAndOrder(product, order);
//      if (existCart != null) {
//        existCart.addQty(cDTO.getQty());
//        cartRepository.save(existCart);
//      } else {
//        Cart cart = new Cart(product, order);
//        cart.setQty(cDTO.getQty());
//        cartList.add(cart);
//        cartRepository.save(cart);
//      }
//    }
//    orderRepository.save(order);

    return order;
  }
}