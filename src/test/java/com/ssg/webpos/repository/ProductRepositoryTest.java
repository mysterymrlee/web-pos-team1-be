package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.dto.hqStock.StockReportResponseDTO;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.service.hqController.method.HqControllerStockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    HqControllerStockService hqControllerStockService;

    @Test
    void contextVoid() {
        List<Product> prodouctList = productRepository.findProductsOrderBySalePriceAsc();
        for(Product product: prodouctList) {
            int salePrice = product.getSalePrice();
            System.out.println(salePrice);
        }
    }

    @Test
    void findProductsOrderBySalePriceAscBySaleState() {
        List<Product> productList = productRepository.findProductsOrderBySalePriceAsc();
        for (Product product : productList) {
            int salePrice = product.getSalePrice();
            int saleState = (int) product.getSaleState();
            System.out.println("sale_price : " + salePrice + "saleState : " +saleState);
        }
    }

    // orderPrice 정렬 테스트
    @Test
    void findProductsOrderByOriginPriceAscBySaleState() {
        List<Product> productList = productRepository.findProductsOrderByOriginPriceAscBySaleState(0);
        for (Product product : productList) {
            int orderPrice = product.getOriginPrice();
            int saleState = (int) product.getSaleState();
            System.out.println("order_price : " + orderPrice + "saleState : " +saleState);
        }
    }

    @Test
    void stockASCBySaleState() {
        List<Product> productList = productRepository.findProductsOrderByStockAscBySaleState(0);
        for (Product product : productList) {
            String productName = product.getName();
            int stock = product.getStock();
            System.out.println(productName + " " + stock);

        }
    }

//    @Test
//    void findProductsOrderBySalePriceDescBySaleState() {
//        List<Product> productList = productRepository.findProductsOrderBySalePriceDescBySaleStateAndStoreId(0,1);
//        List<StockReportResponseDTO> stockReportResponseDTO = hqControllerStockService.getStockReportResponseDTOByQuery(productList);
//        for (StockReportResponseDTO reportResponseDTO : stockReportResponseDTO) {
//            System.out.println(reportResponseDTO);
//        }
//    }
}
