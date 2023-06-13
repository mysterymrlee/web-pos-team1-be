package com.ssg.webpos.dto.delivery;

import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "배송지 추가")
public class DeliveryAddDTO implements Serializable {
  @ApiModelProperty(value = "배송지명")
  private String deliveryName;
  @ApiModelProperty(value = "이름")
  private String userName;
  @ApiModelProperty(value = "주소")
  private String address;
  @ApiModelProperty(value = "휴대폰 번호")
  private String phoneNumber;
  @ApiModelProperty(value = "배송 요청 사항")
  private String requestInfo;
  @ApiModelProperty(value = "배송 상태")
  private DeliveryStatus deliveryStatus;
  @ApiModelProperty(value = "배송 유형")
  private DeliveryType deliveryType;
  @ApiModelProperty(value = "배송 요청 시간")
  private String requestDeliveryTime;
  @ApiModelProperty(value = "배송 일련번호")
  private String serialNumber;
  @ApiModelProperty(value = "우편번호")
  private String postCode;
}
