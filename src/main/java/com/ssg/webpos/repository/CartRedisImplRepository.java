package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartAddDTO;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class CartRedisImplRepository implements CartRedisRepository{
  private RedisTemplate<String, List<CartAddDTO>> redisTemplate;
//  private RedisTemplate<String, CartDto> redisTemplate;

  private HashOperations hashOperations;

//  public CartRedisImplRepository(RedisTemplate<String, CartDto> redisTemplate) {
  public CartRedisImplRepository(RedisTemplate<String, List<CartAddDTO>> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void save(CartAddDTO cartAddDTO) {
    System.out.println("====CartRedisImplRepository.save 시작====");
    System.out.println("before hashOperations.get(\"CART\", cartDto.getPosId()) = " + hashOperations.get("CART", String.valueOf(cartAddDTO.getPosStoreCompositeId())));
    System.out.println("cartDto = " + cartAddDTO);

    // 저장하기 위한 List<CartDto> 선언
    List<CartAddDTO> cartAddDTOList = null;
    if (hashOperations.get("CART", String.valueOf(cartAddDTO.getPosStoreCompositeId())) == null) {
      System.out.println("CartRedisImplRepository.save / if");
      cartAddDTOList = new ArrayList<>();
    } else {
      System.out.println("CartRedisImplRepository.save / else");
      cartAddDTOList = (List<CartAddDTO>) hashOperations.get("CART", String.valueOf(cartAddDTO.getPosStoreCompositeId()));
    }
    // Redis에 List<CartDto> 저장
    cartAddDTOList.add(cartAddDTO);
    hashOperations.put("CART", String.valueOf(cartAddDTO.getPosStoreCompositeId()), cartAddDTOList);

    System.out.println("after hashOperations.get(\"CART\", cartDto.getPosId()) = " + hashOperations.get("CART", String.valueOf(cartAddDTO.getPosStoreCompositeId())));
    System.out.println("cartDto = " + cartAddDTO);
    System.out.println("====CartRedisImplRepository.save 끝====");
  }

  @Override
  public Map<String, List<CartAddDTO>> findAll() throws Exception {
    return hashOperations.entries("CART");
  }

  @Override
  public List<CartAddDTO> findById(String id) {
    return (List<CartAddDTO>) hashOperations.get("CART", id);
  }

  @Override
  public void update(CartAddDTO cartAddDTO) {
    save(cartAddDTO);
  }

  @Override
  public void delete(String id) {
    hashOperations.delete("CART", id);
  }
  @Override
  public void deleteAll() {
    redisTemplate.delete("CART");
  }
}
