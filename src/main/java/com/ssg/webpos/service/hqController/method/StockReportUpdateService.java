package com.ssg.webpos.service.hqController.method;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.dto.hqStock.StockReportUpdateRequestDTO;
import com.ssg.webpos.repository.StockReportRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockReportUpdateService {
    private final StockReportRepository stockReportRepository;
    private final ProductRepository productRepository;
    public void updateStockReport(StockReportUpdateRequestDTO requestDTO) {
        Long productId = (long) requestDTO.getPrdouctId();
        String productName = requestDTO.getProductName();
        int stock = requestDTO.getStock();
        int salePrice = requestDTO.getSalePrice();
        int originPrice = requestDTO.getOriginPrice();
        byte saleState = (byte) requestDTO.getSaleState();

        Optional<Product> product = productRepository.findById(productId);
        product.get().setName(productName);
        product.get().setStock(stock);
        product.get().setSalePrice(salePrice);
        product.get().setOriginPrice(originPrice);
        product.get().setSaleState(saleState);
        productRepository.save(product.get());
    }

}
