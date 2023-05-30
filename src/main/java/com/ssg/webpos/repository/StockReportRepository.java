package com.ssg.webpos.repository;

import com.ssg.webpos.domain.StockReport;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
