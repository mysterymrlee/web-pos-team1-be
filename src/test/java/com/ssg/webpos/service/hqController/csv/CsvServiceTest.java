package com.ssg.webpos.service.hqController.csv;

import com.ssg.webpos.dto.hqSale.HqSaleOrderDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class CsvServiceTest {
  @Autowired
  CsvService csvService;

//  @Test
//  void exportToCsv() {
//    HqSaleOrderDTO hqSaleOrderDTO = new HqSaleOrderDTO();
//    hqSaleOrderDTO.setSerialNumber("00000000");
//    hqSaleOrderDTO.set
//    csvService.exportToCsv();
//  }
}