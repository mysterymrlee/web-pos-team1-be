package com.ssg.webpos.repository.cart;

import com.ssg.webpos.dto.*;
import com.ssg.webpos.dto.cartDto.CartAddRequestDTO;
import com.ssg.webpos.dto.coupon.CouponAddRequestDTO;
import com.ssg.webpos.dto.point.PointDTO;
import com.ssg.webpos.dto.point.PointUseDTO;

import java.util.List;
import java.util.Map;

public interface CartRedisRepository {

  void saveCart(CartAddRequestDTO cartAddRequestDTO);

  void savePoint(PointDTO pointDTO);
  void savePointAmount(PointUseDTO pointUseDTO);

  void saveTest(TestRequestDTO testRequestDTO);

  void saveCoupon(CouponAddRequestDTO CouponAddRequestDTO);
  Map<String, Map<String, List<Object>>>  findAll() throws Exception;
  Map<String, List<Object>> findById(String id);

  List<String> findPhoneNumbersByCompositeId(String compositeId);
  List<Map<String, Object>> findCartItems(String compositeId);
  Long findUserId(String compositeId);
  Long findCouponId(String compositeId);
  String findOrderName(String compositeId);
  Integer findDeductedPrice(String compositeId);
  Integer findPointAmount(String compositeId);
  Integer findTotalOriginPrice(String compositeId);

  Integer findTotalPrice(String compositeId);

  void updatePoint(PointDTO phoneNumberDTO);
  void delete(String id);

  void deleteAll();


}
