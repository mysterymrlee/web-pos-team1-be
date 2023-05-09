package com.ssg.webpos.service;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
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
  private final PosRepository posRepository;

  // 장바구니 담기
  @Transactional
  public void addCart(CartAddDTO cartAddDTO) {
    // pos id로 해당 pos의 order 찾기(05.07 수정)
    Order order = orderRepository.findByPosId(cartAddDTO.getPosStoreCompositeId());
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(cartAddDTO.getPosStoreCompositeId().getPos_id());
    posStoreCompositeId.setStore_id(cartAddDTO.getPosStoreCompositeId().getStore_id());
    Pos pos = posRepository.findById(cartAddDTO.getPosStoreCompositeId()).get();

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
}