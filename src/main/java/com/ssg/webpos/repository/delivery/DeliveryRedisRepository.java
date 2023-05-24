package com.ssg.webpos.repository.delivery;

import com.ssg.webpos.dto.delivery.DeliveryListRedisSelectRequestDTO;
import com.ssg.webpos.dto.delivery.DeliveryRedisAddRequestDTO;
import com.ssg.webpos.dto.delivery.DeliveryAddressDTO;

import java.util.List;
import java.util.Map;

public interface DeliveryRedisRepository {
  void saveDelivery(DeliveryRedisAddRequestDTO deliveryAddRequestDTO);
  void saveSelectedDelivery(DeliveryListRedisSelectRequestDTO deliveryListRedisSelectRequestDTO);
  Map<String, Map<String, List<Object>>> findAll() throws Exception;
  Map<String, List<Object>> findById(String id);
  void delete(String id);
  void deleteAll();
  List<String> findByUserId();
}
