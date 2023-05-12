package com.ssg.webpos.service;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
