package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.dto.cartDto.CartAddDTO;
import com.ssg.webpos.dto.cartDto.CartAddRequestDTO;
import com.ssg.webpos.dto.TestDTO;
import com.ssg.webpos.dto.TestRequestDTO;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController // @ResponseBody + @Controller
@RequestMapping("/api/v1/carts")
public class CartController {

  @Autowired
  CartRedisRepository cartRedisRepository;


  @GetMapping("")
  public ResponseEntity<List<Cart>> getCartList() throws Exception {
    Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
    return new ResponseEntity(all, HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity addCart(@RequestBody @Valid CartAddRequestDTO requestDTO, BindingResult bindingResult) {
    Long posId = requestDTO.getPosId();
    Long storeId = requestDTO.getStoreId();

    List<CartAddDTO> cartItemList = requestDTO.getCartItemList();
    System.out.println("cartItemList = " + cartItemList);

    for (CartAddDTO cartAddDTO : cartItemList) {
      cartAddDTO.setPosStoreCompositeId(new PosStoreCompositeId(posId, storeId));
      cartRedisRepository.saveCart(requestDTO);
      System.out.println("cartAddDTO = " + cartAddDTO);
    }

    System.out.println("bindingResult = " + bindingResult);
    System.out.println("bindingResult.hasErrors() = " + bindingResult.hasErrors());
    return new ResponseEntity(HttpStatus.NO_CONTENT); // 204
  }

  @GetMapping("/delete-redis")
  public ResponseEntity deleteRedis() {
    try {
      cartRedisRepository.deleteAll();
      return new ResponseEntity(HttpStatus.OK);
    } catch (Exception e) {
     return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }
}
