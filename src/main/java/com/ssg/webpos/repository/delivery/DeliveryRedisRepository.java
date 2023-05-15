package com.ssg.webpos.repository.delivery;

import com.ssg.webpos.dto.DeliveryAddDTO;

import java.util.List;
import java.util.Map;

public interface DeliveryRedisRepository {
  void saveDelivery(DeliveryAddDTO deliveryDTO);
  Map<String, Map<String, List<Object>>> findAll() throws Exception;
  Map<String, List<Object>> findById(String id);
  void delete(String id);
  void deleteAll();

  List<String> findByUserId();
}
