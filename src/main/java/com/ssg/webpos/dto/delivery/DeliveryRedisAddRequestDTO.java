package com.ssg.webpos.dto.delivery;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@ApiModel(value = "추가 배송지 redis 요청 DTO")
public class DeliveryRedisAddRequestDTO {
  @ApiModelProperty(value = "store Id")
  private Long storeId;
  @ApiModelProperty(value = "pos Id")
  private Long posId;
  @ApiModelProperty(value = "배송지명")
  private String deliveryName;
  @ApiModelProperty(value = "이름")
  private String userName;
  @ApiModelProperty(value = "주소")
  private String address;
  @ApiModelProperty(value = "휴대폰 번호")
  private String phoneNumber;
  @ApiModelProperty(value = "배송 요청 시간")
  private String requestDeliveryTime;
  @ApiModelProperty(value = "우편번호")
  private String postCode;
  @ApiModelProperty(value = "배송 요청 사항")
  private String requestInfo;
}
