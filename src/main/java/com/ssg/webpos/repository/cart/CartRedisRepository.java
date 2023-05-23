package com.ssg.webpos.repository.cart;

import com.ssg.webpos.dto.*;

import java.util.List;
import java.util.Map;

public interface CartRedisRepository {

  void saveCart(CartAddRequestDTO cartAddRequestDTO);

  void savePoint(PointDTO phoneNumberDTO);
  void savePointAmount(PointUseDTO pointUseDTO);

  void saveCoupon(CouponRequestDTO CouponRequestDTO);
  Map<String, Map<String, List<Object>>>  findAll() throws Exception;
  Map<String, List<Object>> findById(String id);

  List<String> findPhoneNumbersByCompositeId(String compositeId);
  List<Map<String, Object>> findCartItems(String compositeId);
  Long findUserId(String compositeId);
  Long findCouponId(String compositeId);
  Integer findDeductedPrice(String compositeId);
  Integer findPointAmount(String compositeId);

  Integer findTotalPrice(String compositeId);

  void updatePoint(PointDTO phoneNumberDTO);
  void delete(String id);

  void deleteAll();


}
