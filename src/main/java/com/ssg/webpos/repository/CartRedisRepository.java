package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PhoneNumberRequestDTO;

import java.util.List;
import java.util.Map;

public interface CartRedisRepository {

  void save(CartAddDTO cartAddDTO, PhoneNumberRequestDTO phoneNumberRequestDTO);
  Map<String, Map<String, List<Object>>>  findAll() throws Exception;
  Map<String, List<Object>> findById(String id);

  List<String> findAllPhoneNumbers();
  void update(CartAddDTO cartAddDTO, PhoneNumberRequestDTO phoneNumberRequestDTO);
  void delete(String id);

  void deleteAll();
}
