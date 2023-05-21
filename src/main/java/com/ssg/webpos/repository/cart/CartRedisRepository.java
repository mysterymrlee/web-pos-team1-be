package com.ssg.webpos.repository.cart;

import com.ssg.webpos.dto.*;

import java.util.List;
import java.util.Map;

public interface CartRedisRepository {

  void saveCart(CartAddRequestDTO cartAddRequestDTO);

  void savePoint(PointDTO phoneNumberDTO);

  void saveCoupon(CouponDTO couponDTO);
  Map<String, Map<String, List<Object>>>  findAll() throws Exception;
  Map<String, List<Object>> findById(String id);

  List<String> findPhoneNumbersByCompositeId(String compositeId);

  Long findUserId(String compositeId);

  void updatePoint(PointDTO phoneNumberDTO);
  void delete(String id);

  void deleteAll();


}
