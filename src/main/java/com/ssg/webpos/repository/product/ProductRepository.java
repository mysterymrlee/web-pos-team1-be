package com.ssg.webpos.repository.product;

import com.ssg.webpos.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    @Query("select p from Product p join fetch p.event as e" +
            " where e.eventStatus = 1 and p.category =:category")
    List<Product> findByCategoryWithEvent(
            @Param(value = "category") String category
    );
}
