package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartDto;

import java.util.Map;

public interface CartRedisRepository {

  void save(CartDto cartDto);
  Map<String, CartDto> findAll() throws Exception;
  CartDto findById(String id);
  void update(CartDto cartDto);
  void delete(String id);

}
