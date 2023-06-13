package com.ssg.webpos.service.hqController.method;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.StockReport;
import com.ssg.webpos.domain.Store;
import com.ssg.webpos.dto.hqStock.StockReportResponseDTO;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class HqControllerStockServiceTest {
  @Autowired
  HqControllerStockService hqControllerStockService;
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  StockReportRepository stockReportRepository;

  @Test
  void getStockReportResponseDTOList() {
    List<StockReport> stockReportList = new ArrayList<>();
    StockReport stockReport1 = new StockReport();
    stockReport1.setProduct(new Product());
    stockReport1.getProduct().setProductCode("123");
    stockReport1.setStore(new Store());
    stockReport1.getStore().setName("Store 1");
    stockReport1.getProduct().setCategory("Category 1");
    stockReportList.add(stockReport1);

    StockReport stockReport2 = new StockReport();
    stockReport2.setProduct(new Product());
    stockReport2.getProduct().setProductCode("456");
    stockReport2.setStore(new Store());
    stockReport2.getStore().setName("Store 2");
    stockReport2.getProduct().setCategory("Category 2");
    stockReportList.add(stockReport2);

    List<StockReportResponseDTO> result = hqControllerStockService.getStockReportResponseDTOList(stockReportList);

    // Assertions
    assertEquals(2, result.size());
    assertEquals("123", result.get(0).getProductCode());
    assertEquals("Store 1", result.get(0).getStoreName());
    assertEquals("Category 1", result.get(0).getCategory());

    assertEquals("456", result.get(1).getProductCode());
    assertEquals("Store 2", result.get(1).getStoreName());
    assertEquals("Category 2", result.get(1).getCategory());

  }

  @Test
  void getStockReportResponseDTOByQuery() {
    // 테스트용 Product 객체 리스트 생성
    List<Product> productList = new ArrayList<>();

    // Product 1
    Product product1 = new Product();
    product1.setProductCode("123");
    Store store1 = new Store();
    store1.setName("Store 1");
    product1.setStore(store1);
    product1.setCategory("Category 1");
    product1.setName("Product 1");
    product1.setStock(10);
    product1.setSalePrice(1000);
    product1.setOriginPrice(2000);
    product1.setSaleState((byte) 1);
    product1.setId(1L);
    productList.add(product1);

    // Product 2
    Product product2 = new Product();
    product2.setProductCode("456");
    Store store2 = new Store();
    store2.setName("Store 2");
    product2.setStore(store2);
    product2.setCategory("Category 2");
    product2.setName("Product 2");
    product2.setStock(20);
    product2.setSalePrice(2000);
    product2.setOriginPrice(3000);
    product2.setSaleState((byte) 1);
    product2.setId(2L);
    productList.add(product2);

    // HqControllerStockService 객체 생성
    HqControllerStockService hqControllerStockService = new HqControllerStockService(orderRepository, stockReportRepository);

    // getStockReportResponseDTOByQuery 메소드 호출
    List<StockReportResponseDTO> result = hqControllerStockService.getStockReportResponseDTOByQuery(productList);

    // Assertions
    assertEquals(2, result.size());
    assertEquals("123", result.get(0).getProductCode());
    assertEquals("Store 1", result.get(0).getStoreName());
    assertEquals("Category 1", result.get(0).getCategory());

    assertEquals("456", result.get(1).getProductCode());
    assertEquals("Store 2", result.get(1).getStoreName());
    assertEquals("Category 2", result.get(1).getCategory());
  }
}