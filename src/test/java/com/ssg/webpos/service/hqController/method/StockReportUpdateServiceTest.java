package com.ssg.webpos.service.hqController.method;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.dto.hqStock.StockReportUpdateRequestDTO;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class StockReportUpdateServiceTest {
  @Autowired
  StockReportUpdateService stockReportUpdateService;
  @Autowired
  ProductRepository productRepository;

  @Test
  void updateStockReport() {
    // Create sample data
    StockReportUpdateRequestDTO requestDTO = new StockReportUpdateRequestDTO();
    requestDTO.setPrdouctId(50);
    requestDTO.setProductName("Test Product");
    requestDTO.setStock(10);
    requestDTO.setSalePrice(100);
    requestDTO.setOriginPrice(150);
    requestDTO.setSaleState(1);

    stockReportUpdateService.updateStockReport(requestDTO);

  }
}