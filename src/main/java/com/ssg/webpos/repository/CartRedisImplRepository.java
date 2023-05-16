package com.ssg.webpos.repository;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PointDTO;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CartRedisImplRepository implements CartRedisRepository{
  @Autowired
  UserRepository userRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  CartRepository cartRepository;
  private RedisTemplate<String, Map<String, List<Object>>> redisTemplate;


  private HashOperations hashOperations;

  public CartRedisImplRepository(RedisTemplate<String,Map<String, List<Object>>> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }


  public void saveCart (CartAddDTO cartAddDTO) {
    PosStoreCompositeId posStoreCompositeId = cartAddDTO.getPosStoreCompositeId();

    String posId = String.valueOf(posStoreCompositeId.getPos_id());
    String storeId = String.valueOf(posStoreCompositeId.getStore_id());
    String compositeId = storeId + "-" + posId;

    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", compositeId);
    if (posData == null) {
      posData = new HashMap<>();
    }

    List<Object> cartList = posData.get("cartList");
    if (cartList == null) {
      cartList = new ArrayList<>();
    }

    Map<String, Object> cartItem = new HashMap<>();
    cartItem.put("productId", cartAddDTO.getProductId());
    cartItem.put("cartQty", cartAddDTO.getCartQty());

    cartList.add(cartItem);

    int totalPrice = cartAddDTO.getTotalPrice();
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
