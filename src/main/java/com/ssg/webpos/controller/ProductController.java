package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.dto.ProductListResponseDTO;
import com.ssg.webpos.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/{category}")
    public ResponseEntity getProductList(@PathVariable String category) {
        List<Product> productList = productService.findByCategory(category);
        List<ProductListResponseDTO> productListResponseDTOList =
                productList.stream().map(p -> new ProductListResponseDTO(p)).collect(Collectors.toList());
        return new ResponseEntity(productListResponseDTOList, HttpStatus.OK);
    }

}
