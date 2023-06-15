package com.ssg.webpos.service.hqController.csv;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.hqSale.HqSaleOrderDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class CsvServiceTest {
  @Autowired
  CsvService csvService;

  @Test
  void exportToCsv() {
    List<HqSaleOrderDTO> orderDTOList = new ArrayList<>();
    HqSaleOrderDTO hqSaleOrderDTO = new HqSaleOrderDTO();
    hqSaleOrderDTO.setSerialNumber("00000000");
    hqSaleOrderDTO.setStoreName("센텀시티점");
    hqSaleOrderDTO.setOrderDate(LocalDateTime.now());
    hqSaleOrderDTO.setOrderStatus(OrderStatus.SUCCESS);
    hqSaleOrderDTO.setPayMethod(PayMethod.CREDIT_CARD);
    hqSaleOrderDTO.setTotalOriginPrice(10000);
    hqSaleOrderDTO.setTotalPrice(9000);
    hqSaleOrderDTO.setPointUsePrice(200);
    hqSaleOrderDTO.setCouponUsePrice(5000);
    hqSaleOrderDTO.setFinalTotalPrice(3800);
    hqSaleOrderDTO.setCharge(100);
    hqSaleOrderDTO.setProfit(1000);
    orderDTOList.add(hqSaleOrderDTO);


    csvService.exportToCsv(orderDTOList, "test");
    assertDoesNotThrow(() -> csvService.exportToCsv(orderDTOList, "test"));
  }
}