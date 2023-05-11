package com.ssg.webpos.repository.delivery;

import com.ssg.webpos.dto.DeliveryDTO;

import java.util.List;
import java.util.Map;

public interface DeliveryRedisRepository {
  void save(DeliveryDTO deliveryDTO);
  Map<String, Map<String, List<Object>>> findAll() throws Exception;
  Map<String, List<Object>> findById(String id);
  void delete(String id);
  void deleteAll();
}
