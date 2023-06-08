package com.ssg.webpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.gift.GiftDeliveryAddressEntryDTO;
import com.ssg.webpos.dto.gift.GiftSmsDTO;
import com.ssg.webpos.dto.msg.MessageDTO;
import com.ssg.webpos.dto.msg.SmsRequestDTO;
import com.ssg.webpos.dto.msg.SmsResponseDTO;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {
  private final DeliveryRepository deliveryRepository;

  @Value("${naver-cloud-sms.accessKey}")
  private String accessKey;

  @Value("${naver-cloud-sms.secretKey}")
  private String secretKey;

  @Value("${naver-cloud-sms.serviceId}")
  private String serviceId;

  @Value("${naver-cloud-sms.senderPhone}")
  private String phone;

  // Signature 필드 값 생성
  public String makeSignature(String time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
    String space = " ";          // one space
    String newLine = "\n";          // new line
    String method = "POST";          // method
    String url = "/sms/v2/services/" + this.serviceId + "/messages";  // url (include query string)
    String accessKey = this.accessKey;      // access key id (from portal or Sub Account)
    String secretKey = this.secretKey;

    String message = new StringBuilder()
        .append(method)
        .append(space)
        .append(url)
        .append(newLine)
        .append(time)
        .append(newLine)
        .append(accessKey)
        .toString();

    SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(signingKey);

    byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
    String encodeBase64String = Base64.encodeBase64String(rawHmac);

    return encodeBase64String;
  }

  // 선물하기 문자 메시지 전송 시 사용할 정보 가져오기
  public GiftSmsDTO getInfoToUseInGiftSms(Delivery savedDelivery, Order savedOrder) {
    String receiver = savedDelivery.getUserName();
    String sender = savedDelivery.getSender();
    LocalDateTime orderDate = savedOrder.getOrderDate();
    // YYYY-MM-dd 날짜 형식 지정
    String entryDeadline = orderDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
    String giftProductName = savedOrder.getOrderName();

    GiftSmsDTO giftSmsDTO = GiftSmsDTO.builder()
        .receiver(receiver)
        .sender(sender)
        .giftProductName(giftProductName)
        .entryDeadline(entryDeadline)
        .build();
    System.out.println("giftSmsDTO = " + giftSmsDTO);

    return giftSmsDTO;
  }

  // sms 보낼 내용 작성
  public String makeSmsContent(Delivery savedDelivery, Order savedOrder, String giftUrl) {
    GiftSmsDTO smsInfo = getInfoToUseInGiftSms(savedDelivery, savedOrder);

    String giftProductName = smsInfo.getGiftProductName();
    if (giftProductName.length() > 10) {
      giftProductName = giftProductName.substring(0, 10) + "...";
    }
    DeliveryStatus deliveryStatus = savedDelivery.getDeliveryStatus();
    System.out.println("deliveryStatus = " + deliveryStatus);
    String content = "";
    if (savedDelivery.getDeliveryStatus().equals(DeliveryStatus.PROCESS_DELIVERY)) {
      content = "주문하신 상품의 배송이 시작되었습니다.";
    } else if (savedDelivery.getDeliveryStatus().equals(DeliveryStatus.COMPLETE_DELIVERY)) {
      content = "고객님의 상품이 배송 완료되었습니다.";
    } else if (savedDelivery.getDeliveryType().equals(DeliveryType.GIFT)
        && savedDelivery.getDeliveryStatus().equals(DeliveryStatus.COMPLETE_PAYMENT)) {
      content = "[선물이 도착했어요!]\n"
          + smsInfo.getSender() + "님이 " + smsInfo.getReceiver() + "님에게 선물을 보냈습니다.\n"
          + "아래 링크를 통해 선물을 확인하시고 배송지를 입력해주세요.\n\n"
          + "▶ 상품명: " + giftProductName + "\n"
          + "▶ 선물 보러 가기: " + giftUrl + "\n"
          + "▶ 배송지 입력 기한: " + smsInfo.getEntryDeadline() + " 까지\n\n"
          + "* 기한 내에 배송지 미입력 시, 주문이 자동 취소됩니다.";
    }
    System.out.println("content = " + content);
    return content;
  }

  public SmsResponseDTO sendSms(MessageDTO messageDTO, Delivery savedDelivery, Order savedOrder) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
    String time = Long.toString(System.currentTimeMillis());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-ncp-apigw-timestamp", time);
    headers.set("x-ncp-iam-access-key", accessKey);
    headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

    String content = makeSmsContent(savedDelivery, savedOrder, messageDTO.getGiftUrl());
    messageDTO.setContent(content);
    messageDTO.setTo(savedDelivery.getPhoneNumber());

    List<MessageDTO> messages = new ArrayList<>();
    messages.add(messageDTO);

    SmsRequestDTO request = SmsRequestDTO.builder()
        .type("SMS")
        .contentType("COMM")
        .countryCode("82")
        .from(phone)
        .content(messageDTO.getContent())
        .messages(messages)
        .build();

    // 쌓은 바디를 json 형태로 반환
    ObjectMapper objectMapper = new ObjectMapper();
    String body = objectMapper.writeValueAsString(request);
    // jsonBody와 헤더 조립
    HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    // restTemplate로 post 요청 보내고 오류가 없으면 202 코드 반환
    SmsResponseDTO response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + serviceId + "/messages"), httpBody, SmsResponseDTO.class);

    return response;
  }

  public void saveDeliveryAddress(GiftDeliveryAddressEntryDTO giftDeliveryAddressEntryDTO) {
    Delivery findGiftReceiver = deliveryRepository.findByDeliveryTypeAndPhoneNumber(DeliveryType.GIFT, giftDeliveryAddressEntryDTO.getPhoneNumber());
    System.out.println("findGiftReceiver = " + findGiftReceiver);
    findGiftReceiver.setAddress(giftDeliveryAddressEntryDTO.getAddress());
    findGiftReceiver.setPhoneNumber(giftDeliveryAddressEntryDTO.getPhoneNumber());
    findGiftReceiver.setUserName(giftDeliveryAddressEntryDTO.getReceiver());

    deliveryRepository.save(findGiftReceiver);
  }
}
