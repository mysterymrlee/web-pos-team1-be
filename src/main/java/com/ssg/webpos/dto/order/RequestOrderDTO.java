package com.ssg.webpos.dto.order;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RequestOrderDTO {
    private Long storeId;
    private String date;
}
