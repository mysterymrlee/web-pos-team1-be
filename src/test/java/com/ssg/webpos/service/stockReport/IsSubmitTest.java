package com.ssg.webpos.service.stockReport;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.StockReport;
import com.ssg.webpos.dto.stock.StoreIdStockReportResponseDTO;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.service.StockReportService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class IsSubmitTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    StockReportService stockReportService;
    @Autowired
    StockReportRepository stockReportRepository;
    @Test
    @Transactional
    void contextVoid() {
        boolean test = true;
        List<StockReport> list = stockReportService.selectByIsSubmit(test);
        for (StockReport item: list) {
            Hibernate.initialize(item.getProduct());
            Hibernate.initialize(item.getStore());
            System.out.println(item);
        }
    }
    @Test
    @Transactional
    void contextVoid2() {
        List<StockReport> stockReports = stockReportRepository.findByStoreId(1L);
        List<StoreIdStockReportResponseDTO> lists = new ArrayList<>();
        // storeId로 받은 여러개의 stockReport
        for (StockReport stockReport : stockReports) {
            StoreIdStockReportResponseDTO DTO = new StoreIdStockReportResponseDTO();
            DTO.setCurrentStock(stockReport.getCurrentStock());
            DTO.setSubmit(stockReport.isSubmit()); // boolean은 get이 아닌 is 그대로 가져간다.
            Product product = stockReport.getProduct();
            DTO.setProductName(product.getName());
            DTO.setProductSalePrice(product.getSalePrice());
            DTO.setCategory(product.getCategory());
            lists.add(DTO);
        }
    }
}
