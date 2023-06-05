package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.dto.ProductListResponseDTO;
import com.ssg.webpos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    public List<Product> getProductList(@RequestParam("category") String category) {
    List<Product> productList = productService.getProductsBySalesDateAndCategory(category);
        return productList;
    }
}
