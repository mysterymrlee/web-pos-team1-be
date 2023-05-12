package com.ssg.webpos.repository.product;

import com.ssg.webpos.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
}
