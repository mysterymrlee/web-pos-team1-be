package com.ssg.webpos.repository;

import com.ssg.webpos.domain.ProductRequest;
import com.ssg.webpos.dto.stock.stockSubmit.ResponseForDBDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRequestRepository extends JpaRepository<ProductRequest,Long> {
}
