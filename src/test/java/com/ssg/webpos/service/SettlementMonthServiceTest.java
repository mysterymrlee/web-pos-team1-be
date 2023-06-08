package com.ssg.webpos.service;

import com.ssg.webpos.domain.SettlementMonth;
import com.ssg.webpos.domain.Store;
import com.ssg.webpos.repository.settlement.SettlementMonthRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class SettlementMonthServiceTest {
  @Autowired
  SettlementMonthRepository settlementMonthRepository;
  @Autowired
  SettlementMonthService settlementMonthService;
  @Autowired
  StoreRepository storeRepository;

  @Test
  void selectByStoreId_WithValidStoreId_ReturnsList() {
    Store store = new Store();
    store.setName("센텀시티점");
    store.setBranchName("센텀시티점");
    store.setPostCode("11111");
    store.setAddress("부산");
    storeRepository.save(store);
    SettlementMonth settlementMonth = new SettlementMonth(1L, 500000, LocalDate.now(), store, null, 0, 0, 0);
    settlementMonthRepository.save(settlementMonth);
    System.out.println("settlementMonth = " + settlementMonth);
    // Act
    List<SettlementMonth> result = settlementMonthService.selectByStoreId(store.getId());

    // Assert
    assertEquals(1, result.size());
  }



}