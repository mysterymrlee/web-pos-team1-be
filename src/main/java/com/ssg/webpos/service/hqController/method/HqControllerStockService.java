package com.ssg.webpos.service.hqController.method;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.StockReport;
import com.ssg.webpos.domain.Store;
import com.ssg.webpos.dto.hqStock.StockReportResponseDTO;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HqControllerStockService {
    private final OrderRepository orderRepository;
    private final StockReportRepository stockReportRepository;

    // StockReport 객체 리스트를 받는 경우
    public List<StockReportResponseDTO> getStockReportResponseDTOList(List<StockReport> stockReportList) {
        List<StockReportResponseDTO> stockReportResponseDTOList = new ArrayList<>();
        for (StockReport stockReport : stockReportList) {
            StockReportResponseDTO stockReportResponseDTO = new StockReportResponseDTO();
            Product product = stockReport.getProduct();
            stockReportResponseDTO.setProductCode(product.getProductCode());
            Store store = stockReport.getStore();
            stockReportResponseDTO.setStoreName(store.getName());
            stockReportResponseDTO.setCategory(product.getCategory());
            stockReportResponseDTO.setProductName(product.getName());
            stockReportResponseDTO.setStock(product.getStock());
            stockReportResponseDTO.setSalePrice(product.getSalePrice());
            stockReportResponseDTO.setOriginPrice(product.getOriginPrice());
            stockReportResponseDTO.setSaleState(product.getSaleState());
            stockReportResponseDTO.setProductId(product.getId());
            stockReportResponseDTOList.add(stockReportResponseDTO);
        }
        return stockReportResponseDTOList;
    }

    // Product 객체 List를 받는 경우
    public List<StockReportResponseDTO> getStockReportResponseDTOByQuery(List<Product> productList) {
        List<StockReportResponseDTO> stockReportResponseDTOList = new ArrayList<>();
        for (Product product : productList) {
            StockReportResponseDTO stockReportResponseDTO = new StockReportResponseDTO();
            stockReportResponseDTO.setProductCode(product.getProductCode());
            Store store = product.getStore();
            stockReportResponseDTO.setStoreName(store.getName());
            stockReportResponseDTO.setCategory(product.getCategory());
            stockReportResponseDTO.setProductName(product.getName());
            stockReportResponseDTO.setStock(product.getStock());
            stockReportResponseDTO.setSalePrice(product.getSalePrice());
            stockReportResponseDTO.setOriginPrice(product.getOriginPrice());
            stockReportResponseDTO.setSaleState(product.getSaleState());
            stockReportResponseDTO.setProductId(product.getId());
            stockReportResponseDTOList.add(stockReportResponseDTO);
        }
        return stockReportResponseDTOList;
    }


}
