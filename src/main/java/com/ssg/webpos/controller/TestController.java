package com.ssg.webpos.controller;

import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.repository.CartRedisRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Data
@RequestMapping("/api/v1/cart")
public class TestController {


  @Autowired
  private CartRedisRepository cartRedisRepository;

//  @PostMapping
//  public void add(@RequestBody CartAddDTO cartAddDTO){
//    cartRedisRepository.save(cartAddDTO);
//  }



//  public List<CartDto> findAllCarts() throws Exception {
//    Map<String, CartDto> all = cartRedisRepository.findAll();
//    return new ArrayList<>(all.values());
//  }
@DeleteMapping("/clear")
public ResponseEntity<String> clearCart() {
  cartRedisRepository.deleteAll();
  return ResponseEntity.ok("Cart is cleared successfully!");
}


//  @DeleteMapping("/{id}")
//  public void delete(@PathVariable String id) {
//    cartRedisRepository.delete(id);
//  }
//

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
