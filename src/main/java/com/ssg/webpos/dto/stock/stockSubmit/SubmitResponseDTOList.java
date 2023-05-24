package com.ssg.webpos.dto.stock.stockSubmit;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubmitResponseDTOList {
    private Long storeId;
    private List<SubmitResponseDTO> submitResponseDTO;
}
