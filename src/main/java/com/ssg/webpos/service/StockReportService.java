package com.ssg.webpos.service;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.StockReport;
import com.ssg.webpos.repository.StockReportRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockReportService {
    private final StockReportRepository stockReportRepository;

    // is_submit=1인 열들 조회
    // is_submit=1은 IsSubmit이 true
    public List<StockReport> selectByIsSubmit(boolean isSubmit) {
        try{
            List<StockReport> lists = stockReportRepository.findByIsSubmit(isSubmit);
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
