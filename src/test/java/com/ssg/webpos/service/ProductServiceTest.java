package com.ssg.webpos.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class ProductServiceTest {
  @Autowired
  ProductService productService;
  @Test
  void getProductsBySalesDateAndCategory() {
    String category = "과일";
    productService.getProductsBySalesDateAndCategory(category);
  }
}