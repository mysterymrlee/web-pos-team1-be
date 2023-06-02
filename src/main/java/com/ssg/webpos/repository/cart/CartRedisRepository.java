package com.ssg.webpos.repository.cart;

import com.ssg.webpos.dto.cartDto.CartAddRequestDTO;
import com.ssg.webpos.dto.coupon.CouponAddRequestDTO;
import com.ssg.webpos.dto.point.PointDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
public interface CartRedisRepository {

  void saveCart(CartAddRequestDTO cartAddRequestDTO);

  void savePoint(PointDTO pointDTO);
  void saveCoupon(CouponAddRequestDTO CouponAddRequestDTO);
  Map<String, Map<String, List<Object>>>  findAll() throws Exception;
  Map<String, List<Object>> findById(String id);

  String findPhoneNumber(String compositeId);
  List<Map<String, Object>> findCartItems(String compositeId);
  Long findUserId(String compositeId);
  Long findCouponId(String compositeId);
  String findOrderName(String compositeId);
  Integer findDeductedPrice(String compositeId);
  Integer findTotalOriginPrice(String compositeId);

  Integer findTotalPrice(String compositeId);

  void updatePoint(PointDTO phoneNumberDTO);
  void delete(String id);

  void deleteAll();


}
