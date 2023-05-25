package com.ssg.webpos.repository;

import com.ssg.webpos.dto.stock.stockSubmit.ResponseForDBDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ProductRequestRepositoryTest {
  @Autowired
  ProductRequestRepository productRequestRepository;

  @Test
  void contextVoids() {
//    @Test
//    void contextVoids() {
//        List<ResponseForDBDTO> lists = new ArrayList<>();
//        ResponseForDBDTO r1 = new ResponseForDBDTO();
//        r1.setProductRequestId(1L);
//        r1.setQty(1);
//        r1.setProductId(1L);
//        r1.setCurrentStock(1);
//        r1.setCreateTime(LocalDateTime.now());
//        r1.setLastModifiedTime(LocalDateTime.now());
//        r1.setStoreId(1L);
//        ResponseForDBDTO r2 = new ResponseForDBDTO();
//        r2.setProductRequestId(2L);
//        r2.setQty(2);
//        r2.setProductId(1L);
//        r2.setCurrentStock(1);
//        r2.setCreateTime(LocalDateTime.now());
//        r2.setLastModifiedTime(LocalDateTime.now());
//        r2.setStoreId(1L);
//        lists.add(r1);
//        lists.add(r2);
//        productRequestRepository.saveAll(lists);
  }
}