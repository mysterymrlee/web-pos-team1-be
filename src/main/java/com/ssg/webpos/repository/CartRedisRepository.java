package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartAddDTO;

import java.util.List;
import java.util.Map;

public interface CartRedisRepository {

  void save(CartAddDTO cartAddDTO);
  Map<String, List<CartAddDTO>> findAll() throws Exception;
  List<CartAddDTO> findById(String id);
  void update(CartAddDTO cartAddDTO);
  void delete(String id);

  void deleteAll();
}
