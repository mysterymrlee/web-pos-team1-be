package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.CartAddRequestDTO;
import com.ssg.webpos.dto.PhoneNumberDTO;
import com.ssg.webpos.repository.CartRedisRepository;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.service.CartService;
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
    List<CartAddDTO> cartItemList = requestDTO.getCartItemList();
    for (CartAddDTO cartAddDTO : cartItemList) {
      cartRedisRepository.saveCart(cartAddDTO);
      System.out.println("cartAddDTO = " + cartAddDTO);
    }
    System.out.println("bindingResult = " + bindingResult);
    System.out.println("bindingResult.hasErrors() = " + bindingResult.hasErrors()); //
    return new ResponseEntity(HttpStatus.NO_CONTENT); // 204
  }

}
