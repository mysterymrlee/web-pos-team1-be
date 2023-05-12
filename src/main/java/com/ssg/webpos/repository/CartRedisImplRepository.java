package com.ssg.webpos.repository;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PointDTO;
import com.ssg.webpos.dto.PointRedisDTO;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CartRedisImplRepository implements CartRedisRepository{
  private RedisTemplate<String, Map<String, List<Object>>> redisTemplate;
//  private RedisTemplate<String, CartDto> redisTemplate;

  private HashOperations hashOperations;

//  public CartRedisImplRepository(RedisTemplate<String, CartDto> redisTemplate) {
  public CartRedisImplRepository(RedisTemplate<String,Map<String, List<Object>>> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }


  public void saveCart (CartAddDTO cartAddDTO) {
    PosStoreCompositeId posStoreCompositeId = cartAddDTO.getPosStoreCompositeId();

    String posId = String.valueOf(posStoreCompositeId.getPos_id());
    String storeId = String.valueOf(posStoreCompositeId.getStore_id());
    String compositeId = posId + "-" + storeId;

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
    PosStoreCompositeId posStoreCompositeId = pointDTO.getPosStoreCompositeId();
    String posId = String.valueOf(posStoreCompositeId.getPos_id());
    String storeId = String.valueOf(posStoreCompositeId.getStore_id());
    String pointMethod = pointDTO.getPointMethod();
    String compositeId = posId + "-" + storeId;

    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", compositeId);
    if (posData == null) {
      posData = new HashMap<>();
      hashOperations.put("CART", compositeId, posData);
    }

    List<Object> point = posData.get(pointMethod);
    if (point == null) {
      point = new ArrayList<>();
      posData.put(pointMethod, point);
    }

    PointRedisDTO pointRedisDTO = new PointRedisDTO(pointDTO);
    point.add(pointRedisDTO);

    // posData에 pointMethod 추가
    posData.put("pointMethod", Collections.singletonList(pointMethod));// pointMethod 값을 단일 요소를 가진 리스트

    hashOperations.put("CART", compositeId, posData);
  }

  @Override
  public Map<String, Map<String, List<Object>>> findAll() throws Exception {
    Map<String, Map<String, List<Object>>> result = new HashMap<>();
    Map<String, Map<String, List<Object>>> posData = hashOperations.entries("CART");
    for (Map.Entry<String, Map<String, List<Object>>> entry : posData.entrySet()) {
      result.put(entry.getKey(), entry.getValue()
      );
    }
    return result;
  }

  @Override
  public Map<String, List<Object>>  findById(String id) {
    return (Map<String, List<Object>>) hashOperations.get("CART", id);
  }

  @Override
  public List<String> findAllPhoneNumbers() {
    List<String> phoneNumbers = new ArrayList<>();
    Map<String, Map<String, List<Object>>> posDataMap = hashOperations.entries("CART");

    for (Map.Entry<String, Map<String, List<Object>>> entry : posDataMap.entrySet()) {
      Map<String, List<Object>> posData = entry.getValue();
      if (posData != null) {
        List<Object> pointList = posData.get("phoneNumber");
        if (pointList != null && !pointList.isEmpty()) {
          PointDTO phoneNumberDTO = (PointDTO) pointList.get(0);
          phoneNumbers.add(phoneNumberDTO.getPhoneNumber());
        }
      }
    }

    return phoneNumbers;
  }

  @Override
  public void updatePoint(PointDTO pointDTO) {
    hashOperations.put("CART", String.valueOf(pointDTO.getPosStoreCompositeId()), null);
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
