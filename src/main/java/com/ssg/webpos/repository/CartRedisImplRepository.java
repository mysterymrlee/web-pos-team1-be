package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PhoneNumberRequestDTO;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  public void save(CartAddDTO cartAddDTO, PhoneNumberRequestDTO phoneNumberRequestDTO) {
    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", String.valueOf(cartAddDTO.getPosId()));
    if (posData == null) {
      posData = new HashMap<>();
    }

    List<Object> cartList = posData.get("cart");
    List<Object> point = posData.get("point");
    if (cartList == null) {
      cartList = new ArrayList<>();
    }
    if(point == null){
      point = new ArrayList<>();
    }
    cartList.add(cartAddDTO);
    point.add(phoneNumberRequestDTO);
    posData.put("cart", cartList);
    posData.put("point",point);
    System.out.println("posData = " + posData);
    hashOperations.put("CART", String.valueOf(cartAddDTO.getPosId()), posData);
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
  public List<String> findAllPhoneNumbers() {
    List<String> phoneNumbers = new ArrayList<>();
    Map<String, Map<String, List<Object>>> posDataMap = hashOperations.entries("CART");

    for (Map.Entry<String, Map<String, List<Object>>> entry : posDataMap.entrySet()) {
      Map<String, List<Object>> posData = entry.getValue();
      if (posData != null) {
        List<Object> pointList = posData.get("point");
        if (pointList != null && !pointList.isEmpty()) {
          PhoneNumberRequestDTO phoneNumberRequestDTO = (PhoneNumberRequestDTO) pointList.get(0);
          phoneNumbers.add(phoneNumberRequestDTO.getPhoneNumber());
        }
      }
    }

    return phoneNumbers;
  }

  @Override
  public void update(CartAddDTO cartAddDTO, PhoneNumberRequestDTO phoneNumberRequestDTO) {
    hashOperations.put("CART", String.valueOf(cartAddDTO.getPosId()), null);
    save(cartAddDTO, phoneNumberRequestDTO);
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
