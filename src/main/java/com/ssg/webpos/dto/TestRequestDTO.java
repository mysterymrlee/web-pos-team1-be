package com.ssg.webpos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class TestRequestDTO {
  private Long posId;
  private Long storeId;
  private List<TestDTO> testItemList;
}
