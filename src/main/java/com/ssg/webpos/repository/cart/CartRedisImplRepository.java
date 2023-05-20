package com.ssg.webpos.repository.cart;

import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.CartAddRequestDTO;
import com.ssg.webpos.dto.CouponDTO;
import com.ssg.webpos.dto.PointDTO;
import com.ssg.webpos.repository.CouponRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class CartRedisImplRepository implements CartRedisRepository {
  @Autowired
  UserRepository userRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  CartRepository cartRepository;
  @Autowired
  CouponService couponService;
  @Autowired
  CouponRepository couponRepository;
  private RedisTemplate<String, Map<String, List<Object>>> redisTemplate;


  private HashOperations hashOperations;

  public CartRedisImplRepository(RedisTemplate<String,Map<String, List<Object>>> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void saveCart(CartAddRequestDTO cartAddRequestDTO) {
    String posId = String.valueOf(cartAddRequestDTO.getPosId());
    String storeId = String.valueOf(cartAddRequestDTO.getStoreId());
    String compositeId = storeId + "-" + posId;

    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", compositeId);
    if (posData == null) {
      posData = new HashMap<>();
    }

    List<Object> cartList = new ArrayList<>();

    for (CartAddDTO cartAddDTO : cartAddRequestDTO.getCartItemList()) {
      Map<String, Object> cartItem = new HashMap<>();
      cartItem.put("productId", cartAddDTO.getProductId());
      cartItem.put("cartQty", cartAddDTO.getCartQty());
      cartList.add(cartItem);
    }

    int totalPrice = cartAddRequestDTO.getTotalPrice();
    posData.put("totalPrice", Collections.singletonList(totalPrice));

    posData.put("cartList", cartList);
    hashOperations.put("CART", compositeId, posData);
  }



  @Override
  public void savePoint(PointDTO pointDTO) {
    String posId = String.valueOf(pointDTO.getPosId());
    String storeId = String.valueOf(pointDTO.getStoreId());
    String pointMethod = pointDTO.getPointMethod();
    String compositeId = storeId + "-" + posId;

    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", compositeId);
    if (posData == null) {
      posData = new HashMap<>();
      hashOperations.put("CART", compositeId, posData);
    }

    posData.put("pointMethod", Collections.singletonList(pointMethod));

    List<Object> phoneNumbers = new ArrayList<>();
    phoneNumbers.add(pointDTO.getPhoneNumber());
    posData.put("phoneNumber", phoneNumbers);

    String phoneNumber = pointDTO.getPhoneNumber();
    Long userId = userRepository.findByPhoneNumber(phoneNumber).get().getId();
    posData.put("userId", Collections.singletonList(userId));

    hashOperations.put("CART", compositeId, posData);
  }

  @Override
  public void saveCoupon(CouponDTO couponDTO) {
    String storeId = String.valueOf(couponDTO.getStoreId());
    String posId = String.valueOf(couponDTO.getPosId());
    String compositeId = storeId + "-" + posId;
    String serialNumber = couponDTO.getSerialNumber();
    String validationMessage = couponService.validateCoupon(serialNumber);
    boolean couponValid = validationMessage.equals("유효한 쿠폰입니다.");

    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", compositeId);
    if (posData == null) {
      posData = new HashMap<>();
      hashOperations.put("CART", compositeId, posData);
    }
    posData.put("useCoupon", Collections.singletonList(couponValid));
    if (couponValid) {
      Long couponId = couponRepository.findBySerialNumber(serialNumber).get().getId();
      LocalDate deductedPrice = couponRepository.findBySerialNumber(serialNumber).get().getExpiredDate();
      String name = couponRepository.findBySerialNumber(serialNumber).get().getName();

      posData.put("couponId", Collections.singletonList(couponId));
      posData.put("deducatePrice", Collections.singletonList(deductedPrice));
      posData.put("couponName", Collections.singletonList(name));
    }


    hashOperations.put("CART", compositeId, posData);
  }


  @Override
  public Map<String, Map<String, List<Object>>> findAll() throws Exception {
    Map<String, Map<String, List<Object>>> result = new HashMap<>();
    Map<String, Map<String, List<Object>>> posData = hashOperations.entries("CART");
    for (Map.Entry<String, Map<String, List<Object>>> entry : posData.entrySet()) {
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }

  @Override
  public Map<String, List<Object>>  findById(String id) {
    return (Map<String, List<Object>>) hashOperations.get("CART", id);
  }

  @Override
  public List<String> findPhoneNumbersByCompositeId(String compositeId) {
    List<String> phoneNumbers = new ArrayList<>();
    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", compositeId);
    if (posData != null) {
      List<Object> phoneNumberList = posData.get("phoneNumber");
      if (phoneNumberList != null && !phoneNumberList.isEmpty()) {
        for (Object obj : phoneNumberList) {
          String phoneNumber = (String) obj;
          phoneNumbers.add(phoneNumber);
        }
      }
    }

    return phoneNumbers;
  }
  public Long findUserId(String compositeId) {
    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", compositeId);
    if (posData != null) {
      List<Object> userIdList = posData.get("userId");
      if (userIdList != null && !userIdList.isEmpty()) {
        return (Long) userIdList.get(0);
      }
    }
    return null;
  }



  @Override
  public void updatePoint(PointDTO pointDTO) {
    String compositeId = pointDTO.getPosId() + "-" + pointDTO.getStoreId();

    hashOperations.delete("CART", compositeId);
    savePoint(pointDTO);
  }

  @Override
  public void delete(String id) {
    hashOperations.delete("CART", id);
  }
  @Override
  public void deleteAll() {
    redisTemplate.delete("CART");
  }
}
