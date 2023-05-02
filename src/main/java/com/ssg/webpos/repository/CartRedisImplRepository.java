package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class CartRedisImplRepository implements CartRedisRepository{

  private RedisTemplate<String, CartDto> redisTemplate;

  private HashOperations hashOperations;

  public CartRedisImplRepository(RedisTemplate<String, CartDto> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void save(CartDto cartDto) {
    hashOperations.put("CART", cartDto.getId(),cartDto);
  }

  @Override
  public Map<String, CartDto> findAll() throws Exception {
    return hashOperations.entries("CART");
  }

  @Override
  public CartDto findById(String id) {
    return (CartDto) hashOperations.get("CART", id);
  }

  @Override
  public void update(CartDto cartDto) {
    save(cartDto);
  }

  @Override
  public void delete(String id) {
    hashOperations.delete("CART", id);
  }
}
