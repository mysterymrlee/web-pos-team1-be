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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final PosRepository posRepository;


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
    Pos pos = posRepository.findById(cartAddDTO.getPosStoreCompositeId()).get();
    /* cartAddDTOList.get(0).getPosId(); */

    // 주문 생성
    Order order = new Order();
    // serialNumber 생성
    List<Order> orderList = orderRepository.findAll();
    Long newOrderId = orderList.size()+1L;

    order.setOrderDate(orderDTO.getOrderDate());
    String serialNumber = String.format("%03d", newOrderId);
    System.out.println("serialNumber = " + serialNumber); // 세 자릿수로 만들기
    String orderDateStr = orderDTO.getOrderDate().format(DateTimeFormatter.BASIC_ISO_DATE); // 날짜 형식 맞추기 ex) 20230509
    String combinedStr = orderDateStr + serialNumber;
    System.out.println("combinedStr = " + combinedStr);
    order.setSerialNumber(combinedStr);

    System.out.println("order = " + order);
    order.setOrderStatus(OrderStatus.SUCCESS);
    order.setPayMethod(PayMethod.CREDIT_CARD);
    order.setTotalQuantity(orderDTO.getTotalQuantity());
    order.setPos(pos);
    List<Cart> cartList = order.getCartList();

    for (CartAddDTO cDTO : cartAddDTOList) {
      Product product = productRepository.findById(cDTO.getProductId()).get();
      if (product.getStock() < cDTO.getCartQty()) {
        throw new RuntimeException("재고가 부족합니다. 현재 재고 수 : " + product.getStock() + "개");
      }
      int orderQty = cDTO.getCartQty();
      // 장바구니에 담겨있는 상품이 있는지
      Cart existingCart = cartList.stream()
          .filter(cart -> cart.getProduct().equals(product))
          .findFirst()
          .orElse(null);
      // 장바구니에 담겨있는 상품이 있으면 수량만 증가
      if (existingCart != null) {
        int currentQty = existingCart.getQty();
        int newQty = currentQty + orderQty;
        if (newQty > product.getStock()) {
          throw new RuntimeException("재고가 부족합니다. 현재 재고 수 : " + product.getStock() + "개");
        }
        existingCart.setQty(newQty);
      } else {
        if (orderQty > product.getStock()) {
          throw new RuntimeException("재고가 부족합니다. 현재 재고 수 : " + product.getStock() + "개");
        }
        Cart cart = new Cart(product, order);
        cart.setQty(orderQty);
        cartList.add(cart);
      }
      product.minusStockQuantity(orderQty);
      cartRepository.saveAll(cartList);
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