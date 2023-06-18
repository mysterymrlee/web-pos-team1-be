package com.ssg.webpos.service.hqController.csv;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.hqSale.HqListForSaleDTO;
import com.ssg.webpos.dto.hqSale.HqSaleOrderDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class CsvServiceTest {
  @Autowired
  CsvService csvService;

//  @Test
//  void exportToCsv() {
//    List<HqSaleOrderDTO> orderDTOList = new ArrayList<>();
//    HqSaleOrderDTO hqSaleOrderDTO = new HqSaleOrderDTO();
//    hqSaleOrderDTO.setSerialNumber("00000000");
//    hqSaleOrderDTO.setStoreName("센텀시티점");
//    hqSaleOrderDTO.setOrderDate(LocalDateTime.now());
//    hqSaleOrderDTO.setOrderStatus(OrderStatus.SUCCESS);
//    hqSaleOrderDTO.setPayMethod(PayMethod.CREDIT_CARD);
//    hqSaleOrderDTO.setTotalOriginPrice(10000);
//    hqSaleOrderDTO.setTotalPrice(9000);
//    hqSaleOrderDTO.setPointUsePrice(200);
//    hqSaleOrderDTO.setCouponUsePrice(5000);
//    hqSaleOrderDTO.setFinalTotalPrice(3800);
//    hqSaleOrderDTO.setCharge(100);
//    hqSaleOrderDTO.setProfit(1000);
//    orderDTOList.add(hqSaleOrderDTO);
//
//
//    csvService.exportToCsv(orderDTOList, "test");
//    assertDoesNotThrow(() -> csvService.exportToCsv(orderDTOList, "test"));
//  }
@Test
public void testExportToCsv() {
  List<HqSaleOrderDTO> orderDTOList = createSampleOrderDTOList(); // 테스트에 사용할 주문 데이터 리스트 생성
  String fileName = "test.csv"; // 테스트에 사용할 파일명


  csvService.exportToCsv(orderDTOList, fileName);

  // TODO: 테스트에 필요한 파일 내용을 읽어와서 검증하는 로직을 작성해야 합니다.
  // 예를 들어, CSV 파일을 읽어와서 데이터가 정상적으로 기록되었는지 확인하는 코드를 작성할 수 있습니다.
  // Assertions.assertEquals(expectedData, actualData);
}

  private List<HqSaleOrderDTO> createSampleOrderDTOList() {
    // 테스트에 사용할 주문 데이터 리스트 생성 로직을 작성해주세요.
    // 필요한 경우 테스트에 필요한 가상의 주문 데이터를 생성하여 반환합니다.
    List<HqSaleOrderDTO> orderDTOList = new ArrayList<>();

    // 가상의 주문 데이터 추가 예시
    HqSaleOrderDTO hqSaleOrderDTO1 = new HqSaleOrderDTO();
    hqSaleOrderDTO1.setSerialNumber("00000000");
    hqSaleOrderDTO1.setStoreName("센텀시티점");
    hqSaleOrderDTO1.setOrderDate(LocalDateTime.now());
    hqSaleOrderDTO1.setOrderStatus(OrderStatus.SUCCESS);
    hqSaleOrderDTO1.setPayMethod(PayMethod.CREDIT_CARD);
    hqSaleOrderDTO1.setTotalOriginPrice(10000);
    hqSaleOrderDTO1.setTotalPrice(9000);
    hqSaleOrderDTO1.setPointUsePrice(200);
    hqSaleOrderDTO1.setCouponUsePrice(5000);
    hqSaleOrderDTO1.setFinalTotalPrice(3800);
    hqSaleOrderDTO1.setCharge(100);
    hqSaleOrderDTO1.setProfit(1000);
    // 나머지 필드 값들 설정...

    HqSaleOrderDTO hqSaleOrderDTO2 = new HqSaleOrderDTO();
    hqSaleOrderDTO2.setSerialNumber("11111111");
    hqSaleOrderDTO2.setStoreName("대구점");
    hqSaleOrderDTO2.setOrderDate(LocalDateTime.now());
    hqSaleOrderDTO2.setOrderStatus(OrderStatus.SUCCESS);
    hqSaleOrderDTO2.setPayMethod(PayMethod.CREDIT_CARD);
    hqSaleOrderDTO2.setTotalOriginPrice(10000);
    hqSaleOrderDTO2.setTotalPrice(9000);
    hqSaleOrderDTO2.setPointUsePrice(200);
    hqSaleOrderDTO2.setCouponUsePrice(5000);
    hqSaleOrderDTO2.setFinalTotalPrice(3800);
    hqSaleOrderDTO2.setCharge(100);
    hqSaleOrderDTO2.setProfit(1000);
    // 나머지 필드 값들 설정...

    orderDTOList.add(hqSaleOrderDTO1);
    orderDTOList.add(hqSaleOrderDTO2);

    return orderDTOList;
  }

  @Test
  public void testExportToCsvSettlementDay() {
    List<HqListForSaleDTO> HqListForSaleDTOList = createSampleHqListForSaleDTOList(); // 테스트에 사용할 데이터 리스트 생성
    String fileName = "test.csv"; // 테스트에 사용할 파일명

    csvService.exportToCsvSettlementDay(HqListForSaleDTOList, fileName);
  }

  private List<HqListForSaleDTO> createSampleHqListForSaleDTOList() {
    // 테스트에 사용할 데이터 리스트 생성 로직을 작성해주세요.
    // 필요한 경우 테스트에 필요한 가상의 데이터를 생성하여 반환합니다.
    List<HqListForSaleDTO> HqListForSaleDTOList = new ArrayList<>();

    // 가상의 데이터 추가 예시
    HqListForSaleDTO data1 = new HqListForSaleDTO();
    data1.setSettlementDate(LocalDate.of(2023, 6, 12));
    data1.setStoreName("Store A");
    data1.setCharge(200);
    data1.setOriginPrice(5000);
    data1.setSettlementPrice(5000);
    data1.setProfit(200);

    HqListForSaleDTO data2 = new HqListForSaleDTO();
    data2.setSettlementDate(LocalDate.of(2023, 7, 12));
    data1.setStoreName("Store B");
    data1.setCharge(200);
    data1.setOriginPrice(5000);
    data1.setSettlementPrice(5000);
    data1.setProfit(200);

    HqListForSaleDTOList.add(data1);
    HqListForSaleDTOList.add(data2);

    return HqListForSaleDTOList;
  }
}
