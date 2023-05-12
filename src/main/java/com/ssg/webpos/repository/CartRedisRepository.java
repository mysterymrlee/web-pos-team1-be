package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PointDTO;

import java.util.List;
import java.util.Map;

public interface CartRedisRepository {

  void saveCart(CartAddDTO cartAddDTO);

  void savePoint(PointDTO phoneNumberDTO);

  Map<String, Map<String, List<Object>>>  findAll() throws Exception;
  Map<String, List<Object>> findById(String id);

  List<String> findAllPhoneNumbers();
  void updatePoint(PointDTO phoneNumberDTO);
  void delete(String id);

  void deleteAll();


}
