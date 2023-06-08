package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.StockReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StockReportRepository extends JpaRepository<StockReport,Long> {
    List<StockReport> findByIsSubmit(boolean isSubmit);
    List<StockReport> findAll();
    List<StockReport> findByIsSubmitAndStoreId(boolean isSubmit, Long storeId);
    List<StockReport> findByStoreId(Long storeId);
    List<StockReport> findStockReportById(Long id);
    List<StockReport> findByCreatedDate(LocalDateTime createdDate);
    List<StockReport> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<StockReport> findByStoreIdAndCreatedDateBetween(Long storeId, LocalDateTime startDate, LocalDateTime endDate);

    // Product의 saleState 여부에 따라 조회
    List<StockReport> findByProductSaleState(byte saleState);
    List<StockReport> findByStoreIdAndProductSaleState(Long storeId, byte saleState);

    // saleState 여부, salePrice ASC 정렬
    List<StockReport> findByProductSaleStateOrderByProductSalePriceAsc(byte saleState);

    // saleState 여부, salePrice DESC 정렬
    List<StockReport> findByProductSaleStateOrderByProductSalePriceDesc(byte saleState);
    // saleState 여부, originPrice ASC 정렬
    List<StockReport> findByProductSaleStateOrderByProductOriginPriceAsc(byte saleState);
    // saleState 여부, originPrice DESC 정렬
    List<StockReport> findByProductSaleStateOrderByProductOriginPriceDesc(byte saleState);
    // saleState 여부, stock ASC 정렬
    List<StockReport> findByProductSaleStateOrderByProductStockAsc(byte saleState);
    // saleState 여부, stock DESC 정렬
    List<StockReport> findByProductSaleStateOrderByProductStockDesc(byte saleState);
    // originPrice ASC

    // originPrice DESC

    // stock ASC

    // stock DESC

    // salePrice ASC
    List<StockReport> findByOrderByProductSalePriceAsc();
    // salePrice DESC
    List<StockReport> findByOrderByProductSalePriceDesc();
}
