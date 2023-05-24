package com.ssg.webpos.repository.delivery;

import com.ssg.webpos.dto.delivery.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DeliveryRedisImplRepository implements DeliveryRedisRepository {
  private RedisTemplate<String, Map<String, List<Object>>> redisTemplate;
  private HashOperations hashOperations;

  public DeliveryRedisImplRepository(RedisTemplate<String, Map<String, List<Object>>> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  // 배송지 추가 시 캐싱
  public void saveDelivery(DeliveryRedisAddRequestDTO deliveryRedisAddRequestDTO) {
    String storeId = String.valueOf(deliveryRedisAddRequestDTO.getStoreId());
    String posId = String.valueOf(deliveryRedisAddRequestDTO.getPosId());
    String compositeId = posId + "-" + storeId;

    System.out.println("compositeId = " + compositeId);

    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", compositeId);
    if (posData == null) {
      posData = new HashMap<>();
      hashOperations.put("CART", compositeId, posData);
    }

    List<Object> deliveryAddList = new ArrayList<>();

    for (DeliveryRedisAddDTO deliveryAddDTO : deliveryRedisAddRequestDTO.getDeliveryAddList()) {
      Map<String, Object> addDelivery = new HashMap<>();
      addDelivery.put("deliveryName", deliveryAddDTO.getDeliveryName());
      addDelivery.put("userName", deliveryAddDTO.getUserName());
      addDelivery.put("address", deliveryAddDTO.getAddress());
      addDelivery.put("phoneNumber", deliveryAddDTO.getPhoneNumber());
      addDelivery.put("requestInfo", deliveryAddDTO.getRequestInfo());
      addDelivery.put("requestDeliveryTime", deliveryAddDTO.getRequestDeliveryTime());

      deliveryAddList.add(addDelivery);
      System.out.println("addDelivery = " + addDelivery);
    }
    posData.put("deliveryAddList", deliveryAddList);
    System.out.println("deliveryAddList = " + deliveryAddList);
    hashOperations.put("CART", compositeId, posData);
  }

  // 회원 배송지 목록에서 선택된 배송지 캐싱
  @Override
  public void saveSelectedDelivery(DeliveryListRedisSelectRequestDTO deliveryListRedisSelectRequestDTO) {
    String storeId = String.valueOf(deliveryListRedisSelectRequestDTO.getStoreId());
    String posId = String.valueOf(deliveryListRedisSelectRequestDTO.getPosId());
    String compositeId = posId + "-" + storeId;

    System.out.println("compositeId = " + compositeId);

    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", compositeId);
    if (posData == null) {
      posData = new HashMap<>();
      hashOperations.put("CART", compositeId, posData);
    }

    List<Object> selectedDeliveryAddress = new ArrayList<>();

    for(DeliveryListRedisSelectDTO deliveryListRedisSelectDTO : deliveryListRedisSelectRequestDTO.getSelectedDeliveryAddress()) {
      Map<String, Object> selectedAddress = new HashMap<>();
      selectedAddress.put("deliveryName", deliveryListRedisSelectDTO.getDeliveryName());
      selectedAddress.put("userName", deliveryListRedisSelectDTO.getUserName());
      selectedAddress.put("address", deliveryListRedisSelectDTO.getAddress());
      selectedAddress.put("postCode", deliveryListRedisSelectDTO.getPostCode());
      selectedAddress.put("isDefault", deliveryListRedisSelectDTO.isDefault());
      selectedAddress.put("requestDeliveryTime", deliveryListRedisSelectDTO.getRequestDeliveryTime());
      selectedAddress.put("requestInfo", deliveryListRedisSelectDTO.getRequestInfo());

      selectedDeliveryAddress.add(selectedAddress);
      System.out.println("selectedAddress = " + selectedAddress);
    }

    posData.put("selectedDeliveryAddress", selectedDeliveryAddress);
    System.out.println("selectedDeliveryAddress = " + selectedDeliveryAddress);
    hashOperations.put("CART", compositeId, posData);
  }

  // 선물 받는 사람 정보 캐싱
  public void saveGiftRecipientInfo(GiftRequestDTO giftRequestDTO) {
    String posId = String.valueOf(giftRequestDTO.getPosId());
    String storeId = String.valueOf(giftRequestDTO.getStoreId());
    String compositeId = storeId + "-" + posId;

    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("CART", compositeId);
    if (posData == null) {
      posData = new HashMap<>();
      hashOperations.put("CART", compositeId, posData);
    }

    List<Object> giftRecipientInfo = new ArrayList<>();

    for (GiftDTO giftDTO : giftRequestDTO.getGiftRecipientInfo()) {
      Map<String, Object> recipientInfo = new HashMap<>();
      recipientInfo.put("name", giftDTO.getName());
      recipientInfo.put("phoneNumber", giftDTO.getPhoneNumber());
      giftRecipientInfo.add(recipientInfo);
      System.out.println("giftRecipientInfo = " + giftRecipientInfo);
    }

    posData.put("giftRecipientInfo", giftRecipientInfo);
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
  public Map<String, List<Object>> findById(String id) {
    return (Map<String, List<Object>>) hashOperations.get("CART", id);
  }

  @Override
  public List<String> findByUserId() {
    List<String> user = new ArrayList<>();
    Map<String, Map<String, List<Object>>> posDataMap = hashOperations.entries("CART");
    System.out.println("posDataMap = " + posDataMap);
    for (Map.Entry<String, Map<String, List<Object>>> entry : posDataMap.entrySet()) {
      Map<String, List<Object>> posData = entry.getValue();
      if (posData != null) {
        List<Object> userIdList = posData.get("userId");
        if (userIdList != null && !userIdList.isEmpty()) {
          Long userId = (Long) userIdList.get(0); // userId 값을 Long으로 가져옴
          user.add(String.valueOf(userId));
        }
      }
    }
    return user;
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