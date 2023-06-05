package com.ssg.webpos.repository.product;

import com.ssg.webpos.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE :currentDate BETWEEN p.salesStartDate AND p.salesEndDate")
    List<Product> findProductsBySalesDate(@Param("currentDate") LocalDateTime currentDate);
}
