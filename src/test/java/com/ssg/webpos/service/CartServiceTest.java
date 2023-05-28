package com.ssg.webpos.service;

import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.dto.cartDto.CartAddDTO;
import com.ssg.webpos.dto.OrderDTO;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
  @DisplayName("장바구니 삭제 후 주문테이블에서 totalPrice 반영테스트")
  void delCartTest() {
    Long orderId = 88L;
    Long cartId = 187L;
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
  @DisplayName("[주문] 재고량 감소 테스트")
  void addOrderTest() {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(1L);
    posStoreCompositeId.setStore_id(1L);

    Long productId1 = 1L;
    Long productId2 = 2L;
    Long productId3 = 3L;
    List<CartAddDTO> cartAddDTOList = new ArrayList<>();
    // given
    CartAddDTO cartAddDTO1 = new CartAddDTO();
    cartAddDTO1.setPosStoreCompositeId(posStoreCompositeId);
    cartAddDTO1.setProductId(productId1);
    cartAddDTO1.setCartQty(2);
    CartAddDTO cartAddDTO2 = new CartAddDTO();
    cartAddDTO2.setPosStoreCompositeId(posStoreCompositeId);
    cartAddDTO2.setProductId(productId2);
    cartAddDTO2.setCartQty(3);
    CartAddDTO cartAddDTO3 = new CartAddDTO();
    cartAddDTO3.setPosStoreCompositeId(posStoreCompositeId);
    cartAddDTO3.setProductId(productId3);
    cartAddDTO3.setCartQty(4);

    cartAddDTOList.add(cartAddDTO1);
    cartAddDTOList.add(cartAddDTO2);
    cartAddDTOList.add(cartAddDTO3);
    System.out.println("cartAddDTOList = " + cartAddDTOList);
    OrderDTO orderDTO = new OrderDTO();
    orderDTO.setOrderDate(LocalDateTime.now());
    orderDTO.setPosId(1L);
    orderDTO.setStoreId(1L);

    int price = 0, qty = 0;
    for (CartAddDTO cartAddDTO : cartAddDTOList) {
      Product product = productRepository.findById(cartAddDTO.getProductId()).get();
      price += cartAddDTO.getCartQty() * product.getSalePrice();
      qty += cartAddDTO.getCartQty();
    }
    orderDTO.setTotalQuantity(qty);
    orderDTO.setFinalTotalPrice(price);

    // when
    Product product1 = productRepository.findById(productId1).get();
    Product product2 = productRepository.findById(productId2).get();
    Product product3 = productRepository.findById(productId3).get();

    int beforeStock1 = product1.getStock();
    int beforeStock2 = product2.getStock();
    int beforeStock3 = product3.getStock();

    System.out.println("beforeStock1 = " + beforeStock1);
    System.out.println("beforeStock2 = " + beforeStock2);
    System.out.println("beforeStock3 = " + beforeStock3);

    Order savedOrder = cartService.addOrder(cartAddDTOList, cartAddDTO1, orderDTO);
    System.out.println("savedOrder = " + savedOrder);

    int afterStock1 = product1.getStock();
    int afterStock2 = product2.getStock();
    int afterStock3 = product3.getStock();
    System.out.println("afterStock1 = " + afterStock1);
    System.out.println("afterStock3 = " + afterStock3);

    // then
    List<Cart> savedCarts = savedOrder.getCartList();
    for (Cart cart : savedCarts) {
      System.out.println("cart = " + cart);
    }
    System.out.println("savedCarts = " + savedCarts);
    assertEquals(9, savedOrder.getTotalQuantity());
    assertEquals(3, savedCarts.size());

    // 기존에 존재하지 않는 상품의 경우
    Cart savedCart2 = savedCarts.get(2);
    System.out.println("savedCart2 = " + savedCart2);
    assertEquals(productId3, savedCart2.getProduct().getId());
    assertEquals(4, savedCart2.getQty());
    int expectedStock2 = beforeStock3 - savedCart2.getQty();
    System.out.println("expectedStock2 = " + expectedStock2);
    int actualStock2 = productRepository.findById(productId3).get().getStock();
    assertEquals(expectedStock2, actualStock2);
  }

  @Test
  @DisplayName("[주문 취소] 재고량 증가 테스트")
  void cancelOrderTest() {
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
      System.out.println("index = " + index);
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
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(1L);
    posStoreCompositeId.setStore_id(1L);

    Long productId1 = 1L;
    Long productId2 = 2L;
    Long productId3 = 3L;
    List<CartAddDTO> cartAddDTOList = new ArrayList<>();
    // given
    CartAddDTO cartAddDTO1 = new CartAddDTO();
    cartAddDTO1.setPosStoreCompositeId(posStoreCompositeId);
    cartAddDTO1.setProductId(productId1);
    cartAddDTO1.setCartQty(2);
    CartAddDTO cartAddDTO2 = new CartAddDTO();
    cartAddDTO2.setPosStoreCompositeId(posStoreCompositeId);
    cartAddDTO2.setProductId(productId2);
    cartAddDTO2.setCartQty(3);
    CartAddDTO cartAddDTO3 = new CartAddDTO();
    cartAddDTO3.setPosStoreCompositeId(posStoreCompositeId);
    cartAddDTO3.setProductId(productId3);
    cartAddDTO3.setCartQty(4);

    cartAddDTOList.add(cartAddDTO1);
    cartAddDTOList.add(cartAddDTO2);
    cartAddDTOList.add(cartAddDTO3);
    System.out.println("cartAddDTOList = " + cartAddDTOList);
    OrderDTO orderDTO = new OrderDTO();
    orderDTO.setOrderDate(LocalDateTime.now());
    orderDTO.setPosId(1L);
    orderDTO.setStoreId(1L);

    int price = 0, qty = 0;
    for (CartAddDTO cartAddDTO : cartAddDTOList) {
      Product product = productRepository.findById(cartAddDTO.getProductId()).get();
      price += cartAddDTO.getCartQty() * product.getSalePrice();
      qty += cartAddDTO.getCartQty();
    }
    orderDTO.setTotalQuantity(qty);
    orderDTO.setFinalTotalPrice(price);

    // when
    Product product1 = productRepository.findById(productId1).get();
    Product product2 = productRepository.findById(productId2).get();
    Product product3 = productRepository.findById(productId3).get();

    int beforeStock1 = product1.getStock();
    int beforeStock2 = product2.getStock();
    int beforeStock3 = product3.getStock();

    System.out.println("beforeStock1 = " + beforeStock1);
    System.out.println("beforeStock2 = " + beforeStock2);
    System.out.println("beforeStock3 = " + beforeStock3);

    Order savedOrder = cartService.addOrder(cartAddDTOList, cartAddDTO1, orderDTO);
    System.out.println("savedOrder = " + savedOrder);

    int afterStock1 = product1.getStock();
    int afterStock2 = product2.getStock();
    int afterStock3 = product3.getStock();
    System.out.println("afterStock1 = " + afterStock1);
    System.out.println("afterStock3 = " + afterStock3);

    // then
    List<Cart> savedCarts = savedOrder.getCartList();
    for (Cart cart : savedCarts) {
      System.out.println("cart = " + cart);
    }
    System.out.println("savedCarts = " + savedCarts);
    assertEquals(9, savedOrder.getTotalQuantity());
    assertEquals(3, savedCarts.size());

    // 이미 존재하는 상품이 있을 경우
//    Cart savedCart1 = savedCarts.get(0);
//    System.out.println("savedCart1 = " + savedCart1);
//    assertEquals(6, savedCart1.getQty());

//    int expectedStock1 = beforeStock1 - savedCart1.getQty();
//    System.out.println("expectedStock1 = " + expectedStock1);
//    assertEquals(expectedStock1, afterStock1);

    // 기존에 존재하지 않는 상품의 경우
    Cart savedCart2 = savedCarts.get(2);
    System.out.println("savedCart2 = " + savedCart2);
    assertEquals(productId3, savedCart2.getProduct().getId());
    assertEquals(4, savedCart2.getQty());
    int expectedStock2 = beforeStock3 - savedCart2.getQty();
    System.out.println("expectedStock2 = " + expectedStock2);
    int actualStock2 = productRepository.findById(productId3).get().getStock();
    assertEquals(expectedStock2, actualStock2);
    return savedOrder.getId();
  }


}
