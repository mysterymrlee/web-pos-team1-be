//package com.ssg.webpos.service;
//
//import com.ssg.webpos.domain.Cart;
//import com.ssg.webpos.domain.Order;
//import com.ssg.webpos.domain.Product;
//import com.ssg.webpos.domain.PosStoreCompositeId;
//import com.ssg.webpos.dto.CartAddDTO;
//import com.ssg.webpos.dto.OrderDTO;
//import com.ssg.webpos.repository.CartRedisImplRepository;
//import com.ssg.webpos.repository.cart.CartRepository;
//import com.ssg.webpos.repository.order.OrderRepository;
//import com.ssg.webpos.repository.pos.PosRepository;
//import com.ssg.webpos.repository.product.ProductRepository;
//import com.ssg.webpos.repository.store.StoreRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@Transactional
//class CartRedisServiceTest {
//
//  @Autowired
//  OrderRepository orderRepository;
//  @Autowired
//  ProductRepository productRepository;
//  @Autowired
//  CartRepository cartRepository;
//
//  @Autowired
//  StoreRepository storeRepository;
//  @Autowired
//  PosRepository posRepository;
//  @Autowired
//  CartService cartService;
//
//  @Autowired
//  CartRedisService cartRedisService;
//
//  @Autowired
//  CartRedisImplRepository cartRedisRepository;
//
//
//  @Test
//  void addOrderTest() throws Exception {
//    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
//    posStoreCompositeId.setPos_id(1L);
//    posStoreCompositeId.setStore_id(1L);
//
//    Long productId1 = 3L;
//    Long productId2 = 3L;
//    Long productId3 = 9L;
//    List<CartAddDTO> cartAddDTOList = new ArrayList<>();
//    // given
//
//    CartAddDTO cartAddDTO1 = new CartAddDTO(posStoreCompositeId, productId1, 5);
//    CartAddDTO cartAddDTO2 = new CartAddDTO(posStoreCompositeId, productId2, 1);
//    CartAddDTO cartAddDTO3 = new CartAddDTO(posStoreCompositeId, productId3, 10);
//
//    //redis 저장
//    cartRedisRepository.saveCart(cartAddDTO1);
//    cartRedisRepository.saveCart(cartAddDTO2);
//    cartRedisRepository.saveCart(cartAddDTO3);
//
//
//    Map<String, Map<String, List<Object>>> redisAll = cartRedisRepository.findAll();
//    System.out.println("redisAll = " + redisAll);
//
//    Map<String, List<Object>> byId = cartRedisRepository.findById(String.valueOf(cartAddDTO1.getPosStoreCompositeId()));
//    System.out.println("byId = " + byId);
//
//    cartAddDTOList.add(cartAddDTO1);
//    cartAddDTOList.add(cartAddDTO2);
//    cartAddDTOList.add(cartAddDTO3);
//    System.out.println("cartAddDTOList = " + cartAddDTOList);
//    OrderDTO orderDTO = new OrderDTO();
//    orderDTO.setOrderDate(LocalDateTime.now());
//
//    int price = 0, qty = 0;
//    for (CartAddDTO cartAddDTO : cartAddDTOList) {
//      Product product = productRepository.findById(cartAddDTO.getProductId()).get();
//      price += cartAddDTO.getCartQty() * product.getSalePrice();
//      qty += cartAddDTO.getCartQty();
//    }
//    orderDTO.setTotalQuantity(qty);
//    orderDTO.setFinalTotalPrice(price);
//
//    // when
//    Product product1 = productRepository.findById(productId1).get();
//    Product product2 = productRepository.findById(productId2).get();
//    Product product3 = productRepository.findById(productId3).get();
//
//    int beforeStock1 = product1.getStock();
//    int beforeStock2 = product2.getStock();
//    int beforeStock3 = product3.getStock();
//
//    System.out.println("beforeStock1 = " + beforeStock1);
//    System.out.println("beforeStock2 = " + beforeStock2);
//    System.out.println("beforeStock3 = " + beforeStock3);
//
//    Order savedOrder = cartService.addOrder(cartAddDTOList, cartAddDTO1, orderDTO);
//    System.out.println("savedOrder = " + savedOrder);
//
//    int afterStock1 = product1.getStock();
//    int afterStock2 = product2.getStock();
//    int afterStock3 = product3.getStock();
//    System.out.println("afterStock1 = " + afterStock1);
//    System.out.println("afterStock3 = " + afterStock3);
//
//    // then
//    List<Cart> savedCarts = savedOrder.getCartList();
//    for (Cart cart : savedCarts) {
//      System.out.println("cart = " + cart);
//    }
//    System.out.println("savedCarts = " + savedCarts);
//    assertEquals(16, savedOrder.getTotalQuantity());
//    assertEquals(2, savedCarts.size());
//
//    // 이미 존재하는 상품이 있을 경우
//    Cart savedCart1 = savedCarts.get(0);
//    System.out.println("savedCart1 = " + savedCart1);
//    assertEquals(6, savedCart1.getQty());
//
//    int expectedStock1 = beforeStock1 - savedCart1.getQty();
//    System.out.println("expectedStock1 = " + expectedStock1);
//    assertEquals(expectedStock1, afterStock1);
//
//    // 기존에 존재하지 않는 상품의 경우
//    Cart savedCart2 = savedCarts.get(1);
//    System.out.println("savedCart2 = " + savedCart2);
//    assertEquals(productId3, savedCart2.getProduct().getId());
//    assertEquals(10, savedCart2.getQty());
//    int expectedStock2 = beforeStock3 - savedCart2.getQty();
//    System.out.println("expectedStock2 = " + expectedStock2);
//    int actualStock2 = productRepository.findById(productId3).get().getStock();
//    assertEquals(expectedStock2, actualStock2);
//  }
//}
