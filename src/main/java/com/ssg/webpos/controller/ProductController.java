package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.dto.ProductListResponseDTO;
import com.ssg.webpos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
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
        List<Product> productListIsEvent = productService.findByCategoryWithEvent(category);

        List<Product> tempProductList = new ArrayList<>();
        for (Product product : productList) {
            tempProductList.add(product);
        }
        for (Product product : productList) {
            for (Product productIsEvent : productListIsEvent) {
                if (product.getProductCode().equals(productIsEvent.getProductCode())) {
                    tempProductList.remove(product);
                }
            }
        }
        for (Product productIsEvent : productListIsEvent) {
            tempProductList.add(productIsEvent);
        }
        List<ProductListResponseDTO> productListResponseDTOList =
                tempProductList.stream()
                        .sorted((a, b) -> a.getName().compareTo(b.getName())) // 상품이름 기준 오름차순
                        .map(p -> new ProductListResponseDTO(p, p.getEvent() != null)).collect(Collectors.toList());

        return new ResponseEntity(productListResponseDTOList, HttpStatus.OK);
    }
    private boolean isCurrentEvent(String productCode, List<Product> productListIsEvent) {
        for (Product product : productListIsEvent) {
            if (productCode.equals(product.getProductCode())) {
                return true;
            }
        }
        return false;
    }

}
