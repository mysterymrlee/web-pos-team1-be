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

  // 주문 생성 후 장바구니 상품들 주문에 추가 - DB에 저장
  @Transactional
  public Order addOrder(List<CartAddDTO> cartAddDTOList, CartAddDTO cartAddDTO, OrderDTO orderDTO) {
    Pos pos = posRepository.findById(cartAddDTO.getPosId()).get();
    /* cartAddDTOList.get(0).getPosId(); */

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
      if (product.getStock() < cDTO.getQty()) {
        throw new RuntimeException("재고가 부족합니다. 현재 재고 수 : " + product.getStock() + "개");
      }
      product.minusStockQuantity(cDTO.getQty());

      Cart cart = new Cart(product, order);
      cart.setQty(cDTO.getQty());
      cartList.add(cart);

      cartRepository.save(cart);
    }
    orderRepository.save(order);

    return order;
  }

  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId).get();
    order.setOrderStatus(OrderStatus.CANCEL);

    // 재고 수량 증가
    List<Cart> cartList = order.getCartList();
    for (Cart cart : cartList) {
      Product product = cart.getProduct();
      int qty = cart.getQty();
      product.plusStockQuantity(qty);
    }

    orderRepository.save(order);
  }
}