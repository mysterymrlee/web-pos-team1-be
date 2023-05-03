package com.ssg.webpos.controller;

import com.ssg.webpos.dto.CartDto;
import com.ssg.webpos.repository.CartRedisRepository;
import com.ssg.webpos.repository.RedisRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Data
public class TestController {

  @Autowired
  private CartRedisRepository cartRedisRepository;

  @PostMapping
  public void add(@RequestBody CartDto cartDto){
    cartRedisRepository.save(cartDto);
  }


  public Map<String, CartDto> all() throws Exception {
    return cartRedisRepository.findAll();
  }
  @GetMapping
  public List<CartDto> findAllCarts() throws Exception {
    Map<String, CartDto> all = cartRedisRepository.findAll();
    return new ArrayList<>(all.values());
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable String id) {
    cartRedisRepository.delete(id);
  }


}
//  @Autowired
//  RedisRepository redisRepository;
//
//  public String getAll(){
//    return redisRepository.findAll().toString();
//  }
//  @GetMapping
//  public Optional<CartDto> getById(){
//    return redisRepository.findById("1");
//  }
//  @PostMapping
//  public String set(@RequestBody CartDto person){
//    return redisRepository.save(person).toString();
//  }
