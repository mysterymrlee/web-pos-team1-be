package com.ssg.webpos.repository.delivery;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.DeliveryAddDTO;
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

  public void saveDelivery(@NotNull DeliveryAddDTO deliveryDTO) {
    String storeId = String.valueOf(deliveryDTO.getStoreId());
    String posId = String.valueOf(deliveryDTO.getPosId());
    String compositeId = posId + "-" + storeId;

    System.out.println("compositeId = " + compositeId);

    Map<String, List<Object>> posData = (Map<String, List<Object>>) hashOperations.get("DELIVERY", compositeId );
    if (posData == null) {
      posData = new HashMap<>();
    }

    List<Object> deliveryList = new ArrayList<>();
    deliveryDTO.setDeliveryType(DeliveryType.DELIVERY);
    deliveryList.add(deliveryDTO);
    posData.put("delivery", deliveryList);
    System.out.println("posData = " + posData);
    hashOperations.put("DELIVERY", compositeId, posData);
  }

  @Override
  public Map<String, Map<String, List<Object>>> findAll() throws Exception {
    Map<String, Map<String, List<Object>>> result = new HashMap<>();
    Map<String, Map<String, List<Object>>> posData = hashOperations.entries("DELIVERY");
    for (Map.Entry<String, Map<String, List<Object>>> entry : posData.entrySet()) {
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }

  @Override
  public Map<String, List<Object>> findById(String id) {
    return (Map<String, List<Object>>) hashOperations.get("DELIVERY", id);
  }

  @Override
  public void delete(String id) {
    hashOperations.delete("DELIVERY", id);
  }

  @Override
  public void deleteAll() {
    redisTemplate.delete("DELIVERY");
  }
}
