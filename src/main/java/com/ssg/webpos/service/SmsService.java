package com.ssg.webpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.dto.gift.GiftSmsDTO;
import com.ssg.webpos.dto.gift.GiftSmsRequestDTO;
import com.ssg.webpos.dto.gift.GiftSmsResponseDTO;
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
  private final OrderRepository orderRepository;

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
  public GiftSmsDTO getInfoToUseInGiftSms(Order savedOrder) {
    Delivery savedDelivery = savedOrder.getDelivery();
    System.out.println("savedDelivery = " + savedDelivery);

    String receiver = savedDelivery.getUserName();
    String sender = savedDelivery.getSender();

    LocalDateTime orderDate = savedOrder.getOrderDate();
    String orderSerialNumber = savedOrder.getSerialNumber();

    // YYYY-MM-dd 날짜 형식 지정
    String entryDeadline = orderDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
    String giftProductName = savedOrder.getOrderName();

    GiftSmsDTO giftSmsDTO = GiftSmsDTO.builder()
        .receiver(receiver)
        .sender(sender)
        .giftProductName(giftProductName)
        .entryDeadline(entryDeadline)
        .orderSerialNumber(orderSerialNumber)
        .build();
    System.out.println("giftSmsDTO = " + giftSmsDTO);

    return giftSmsDTO;
  }

  // sms 보낼 내용 작성
  public String makeSmsContent(Order savedOrder) {
//    GiftSmsDTO smsInfo = getInfoToUseInGiftSms(savedOrder);
    String giftUrl = "http://{배포된FE주소}/enter-gift-address/" + savedOrder.getSerialNumber();
    String receiptUrl = "http://{배포된링크}/" + savedOrder.getMerchantUid();
    System.out.println("giftUrl = " + giftUrl);

    // 상품명이 10자를 초과하면 ...으로 표시
    String orderName = savedOrder.getOrderName();
    if (orderName.length() > 10) {
      orderName = orderName.substring(0, 10) + "...";
    }

    Delivery savedDelivery = savedOrder.getDelivery();

    String content = "";
    if (savedDelivery != null) {
      if (savedDelivery.getDeliveryStatus().equals(DeliveryStatus.PROCESS_DELIVERY)) {
        // 배송 시작
        content = "[배송 출발]\n"
            + "고객님의 소중한 상품이 배송 예정입니다.\n"
            + "- 상품명: " + savedOrder.getOrderName() + "\n"
            + "- 배송 예정 시간: " + savedDelivery.getRequestDeliveryTime();

      } else if (savedDelivery.getDeliveryStatus().equals(DeliveryStatus.COMPLETE_DELIVERY)) {
        // 배송 완료
        content = "고객님의 상품이 배송 완료되었습니다.";
      } else if (savedDelivery.getDeliveryType().equals(DeliveryType.GIFT)
          && savedDelivery.getDeliveryStatus().equals(DeliveryStatus.COMPLETE_PAYMENT)) {
        // 선물하기
        content = "[선물이 도착했어요!]\n"
            + savedDelivery.getSender() + "님이 " + savedDelivery.getUserName() + "님에게 선물을 보냈습니다.\n"
            + "아래 링크를 통해 선물을 확인하시고 배송지를 입력해주세요.\n\n"
            + "▶ 상품명: " + orderName + "\n"
            + "▶ 선물 보러 가기: " + giftUrl + "\n"
            + "▶ 배송지 입력 기한: " + savedOrder.getOrderDate() + " 까지\n\n"
            + "* 기한 내에 배송지 미입력 시, 주문이 자동 취소됩니다.";
//      String content1 = "▶ 선물 보러 가기: " + url;
      }
    } else {
      if (savedOrder.getOrderStatus().equals(OrderStatus.CANCEL)) {
        // 주문 취소
//          content = savedDelivery.getUserName() + "님께서 주문하신 " + orderName + " 주문 취소가 완료되었습니다.";
          content = savedOrder.getUser().getName() +"님께서 주문하신\n " + orderName + " 주문 취소가 완료되었습니다.";

      } else if (savedOrder.getOrderStatus().equals(OrderStatus.SUCCESS) && savedOrder.getUser().getId() != null) {
        // 회원이 결제 완료하면 전자 영수증 발급 문자 전송
        content = "전자 영수증이 발급되었습니다.\n" +
//            "상세한 거래내역은 영수증 상세보기 링크를 통해 확인해주세요.\n\n" +
            "- 영수증 상세 보기: " + receiptUrl;
      }
    }
    System.out.println("content = " + content);
    return content;
  }

  // 문자 메시지 전송
  public SmsResponseDTO sendSms(MessageDTO messageDTO, Delivery savedDelivery, Order savedOrder) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
    String time = Long.toString(System.currentTimeMillis());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-ncp-apigw-timestamp", time);
    headers.set("x-ncp-iam-access-key", accessKey);
    headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

    String content = makeSmsContent(savedOrder);
    messageDTO.setContent(content);
    if (savedDelivery != null) {
      messageDTO.setTo(savedDelivery.getPhoneNumber());
    } else {
      String phoneNumber = savedOrder.getUser().getPhoneNumber();
      System.out.println("phoneNumber = " + phoneNumber);
      messageDTO.setTo(phoneNumber);
    }

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

  // 배송지 입력에 필요한 정보 가져오기
  public GiftSmsResponseDTO getGiftInfo(String orderSerialNumber) {
    Order findOrder = orderRepository.findBySerialNumber(orderSerialNumber);
    System.out.println("findOrder = " + findOrder);
    Delivery findDelivery = deliveryRepository.findById(findOrder.getDelivery().getId()).get();
    GiftSmsResponseDTO giftSmsResponseDTO = GiftSmsResponseDTO.builder()
        .orderName(findOrder.getOrderName())
        .orderPrice(findOrder.getFinalTotalPrice())
        .receiver(findDelivery.getUserName())
        .sender(findDelivery.getSender())
        .phoneNumber(findDelivery.getPhoneNumber())
        .build();
    System.out.println("giftSmsResponseDTO = " + giftSmsResponseDTO);
    return giftSmsResponseDTO;
  }

  // 선물 받는 사람이 입력한 배송지 정보 DB에 저장
  public void saveDeliveryAddress(GiftSmsRequestDTO giftSmsRequestDTO) {
    Order findOrder = orderRepository.findBySerialNumber(giftSmsRequestDTO.getOrderSerialNumber());
    System.out.println("findOrder = " + findOrder);
    Delivery findGiftReceiver = findOrder.getDelivery();

    System.out.println("findGiftReceiver = " + findGiftReceiver);
    findGiftReceiver.setAddress(giftSmsRequestDTO.getAddress());
    findGiftReceiver.setPhoneNumber(giftSmsRequestDTO.getPhoneNumber());
    findGiftReceiver.setUserName(giftSmsRequestDTO.getReceiver());
    findGiftReceiver.setPostCode(giftSmsRequestDTO.getPostCode());

    deliveryRepository.save(findGiftReceiver);
  }
}
