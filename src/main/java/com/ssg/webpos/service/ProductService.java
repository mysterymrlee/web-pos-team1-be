package com.ssg.webpos.service;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public List<Product> getProductsBySalesDateAndCategory(String category) {
        LocalDateTime currentDate = LocalDateTime.now();

        List<Product> allProducts = productRepository.findProductsBySalesDate(currentDate);
        System.out.println("allProducts = " + allProducts);
        List<Product> filteredProducts = allProducts.stream()
            .filter(product -> product.getCategory().equals(category))
            .collect(Collectors.toList());
        System.out.println("filteredProducts = " + filteredProducts);
        return filteredProducts;

    }

}
