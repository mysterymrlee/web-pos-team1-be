package com.ssg.webpos.dto.stock.stockSubmit;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubmitRequestDTOList {
    private Long storeId;
    private List<SubmitRequestDTO> submitRequestDTOList = new ArrayList<>();
}
