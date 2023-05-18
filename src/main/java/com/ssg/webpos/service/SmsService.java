//package com.ssg.webpos.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ssg.webpos.dto.msg.MessageDTO;
//import com.ssg.webpos.dto.msg.SmsRequestDTO;
//import com.ssg.webpos.dto.msg.SmsResponseDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.codec.binary.Base64;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.configurationprocessor.json.JSONArray;
//import org.springframework.boot.configurationprocessor.json.JSONException;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.net.http.HttpHeaders;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class SmsService {
//  @Value("${naver-cloud-sms.accessKey}")
//  private String accessKey;
//
//  @Value("${naver-cloud-sms.secretKey}")
//  private String secretKey;
//
//  @Value("${naver-cloud-sms.serviceId}")
//  private String serviceId;
//
//  @Value("${naver-cloud-sms.senderPhone}")
//  private String phone;
//
////  public String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
////    String space = " ";
////    String newLine = "\n";
////    String method = "POST";
////    String url = "/sms/v2/services/"+ this.serviceId+"/messages";
////    String timestamp = time.toString();
////    String accessKey = this.accessKey;
////    String secretKey = this.secretKey;
////
////    String message = new StringBuilder()
////        .append(method)
////        .append(space)
////        .append(url)
////        .append(newLine)
////        .append(timestamp)
////        .append(newLine)
////        .append(accessKey)
////        .toString();
////
////    SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
////    Mac mac = Mac.getInstance("HmacSHA256");
////    mac.init(signingKey);
////
////    byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
////    String encodeBase64String = Base64.encodeBase64String(rawHmac);
////
////    return encodeBase64String;
////  }
//
//  // https://api.ncloud-docs.com/docs/common-ncpapi
//  private String makeSignature(String url, String timestamp, String method, String accessKey, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
//    String space = " ";                    // one space
//    String newLine = "\n";                 // new line
//
//
//    String message = new StringBuilder()
//        .append(method)
//        .append(space)
//        .append(url)
//        .append(newLine)
//        .append(timestamp)
//        .append(newLine)
//        .append(accessKey)
//        .toString();
//
//    SecretKeySpec signingKey;
//    String encodeBase64String;
//    try {
//      signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
//      Mac mac = Mac.getInstance("HmacSHA256");
//      mac.init(signingKey);
//      byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
//      encodeBase64String = Base64.encodeBase64String(rawHmac);
//      System.out.println("encodeBase64String = " + encodeBase64String);
//    } catch (UnsupportedEncodingException e) {
//      // TODO Auto-generated catch block
//      encodeBase64String = e.toString();
//    }
//
//    return encodeBase64String;
//  }
//
//  private void sendSMS() throws JSONException {
//    String hostNameUrl = "https://sens.apigw.ntruss.com";        // 호스트 URL
//    String requestUrl = "/sms/v2/services/";                      // 요청 URL
//    String requestUrlType = "/messages";                          // 요청 URL
//    String accessKey = this.accessKey;
//    String secretKey = this.secretKey; // 2차 인증을 위해 서비스마다 할당되는 service secret key	// Service Key : https://www.ncloud.com/mypage/manage/info > 인증키 관리 > Access Key ID
//    String serviceId = this.serviceId;      // 프로젝트에 할당된 SMS 서비스 ID							// service ID : https://console.ncloud.com/sens/project > Simple & ... > Project > 서비스 ID
//    String method = "POST";                      // 요청 method
//    String timestamp = Long.toString(System.currentTimeMillis());  // current timestamp (epoch)
//    requestUrl += serviceId + requestUrlType;
//    String apiUrl = hostNameUrl + requestUrl;
//
//    // JSON 을 활용한 body data 생성
//    JSONObject bodyJson = new JSONObject();
//    JSONObject toJson = new JSONObject();
//    JSONArray toArr = new JSONArray();
//
//    //toJson.put("subject","");							// Optional, messages.subject	개별 메시지 제목, LMS, MMS에서만 사용 가능
//    //toJson.put("content","sms test in spring 111");	// Optional, messages.content	개별 메시지 내용, SMS: 최대 80byte, LMS, MMS: 최대 2000byte
//    toJson.put("to", "01032244099");            // Mandatory(필수), messages.to	수신번호, -를 제외한 숫자만 입력 가능
//    toArr.put(toJson);
//
//    bodyJson.put("type", "SMS");              // Madantory, 메시지 Type (SMS | LMS | MMS), (소문자 가능)
//    bodyJson.put("contentType","COMM");          // Optional, 메시지 내용 Type (AD | COMM) * AD: 광고용, COMM: 일반용 (default: COMM) * 광고용 메시지 발송 시 불법 스팸 방지를 위한 정보통신망법 (제 50조)가 적용됩니다.
//    //bodyJson.put("countryCode","82");					// Optional, 국가 전화번호, (default: 82)
//    bodyJson.put("from", "01049922047");          // Mandatory, 발신번호, 사전 등록된 발신번호만 사용 가능
//    //bodyJson.put("subject","");						// Optional, 기본 메시지 제목, LMS, MMS에서만 사용 가능
//    bodyJson.put("content", "테스트 메시지입니다.");  // Mandatory(필수), 기본 메시지 내용, SMS: 최대 80byte, LMS, MMS: 최대 2000byte
//    bodyJson.put("messages", toArr);          // Mandatory(필수), 아래 항목들 참조 (messages.XXX), 최대 1,000개
//
//    //String body = bodyJson.toJSONString();
//    String body = bodyJson.toString();
//
//    System.out.println(body);
//
//    try {
//      URL url = new URL(apiUrl);
//
//      HttpURLConnection con = (HttpURLConnection) url.openConnection();
//      con.setUseCaches(false);
//      con.setDoOutput(true);
//      con.setDoInput(true);
//      con.setRequestProperty("content-type", "application/json");
//      con.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
//      con.setRequestProperty("x-ncp-iam-access-key", accessKey);
//      con.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(requestUrl, timestamp, method, accessKey, secretKey));
//      con.setRequestMethod(method);
//      con.setDoOutput(true);
//      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//
//      wr.write(body.getBytes());
//      wr.flush();
//      wr.close();
//
//      int responseCode = con.getResponseCode();
//      BufferedReader br;
//      System.out.println("responseCode" + " " + responseCode);
//      if (responseCode == 202) { // 정상 호출
//        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//      } else { // 에러 발생
//        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
//      }
//
//      String inputLine;
//      StringBuffer response = new StringBuffer();
//      while ((inputLine = br.readLine()) != null) {
//        response.append(inputLine);
//      }
//      br.close();
//
//      System.out.println(response.toString());
//
//    } catch (Exception e) {
//      System.out.println(e);
//    }
//  }
//
////  public SmsResponseDTO sendSms(MessageDTO messageDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
////    Long time = System.currentTimeMillis();
////
////    HttpHeaders headers = new HttpHeaders();
////    headers.setContentType(MediaType.APPLICATION_JSON);
////    headers.set("x-ncp-apigw-timestamp", time.toString());
////    headers.set("x-ncp-iam-access-key", accessKey);
////    headers.set("x-ncp-apigw-signature-v2", makeSignature(time));
////
////    List<MessageDTO> messages = new ArrayList<>();
////    messages.add(messageDto);
////
////    SmsRequestDTO request = SmsRequestDTO.builder()
////        .type("SMS")
////        .contentType("COMM")
////        .countryCode("82")
////        .from(phone)
////        .content(messageDto.getContent())
////        .messages(messages)
////        .build();
////
////    ObjectMapper objectMapper = new ObjectMapper();
////    String body = objectMapper.writeValueAsString(request);
////    HttpEntity<String> httpBody = new HttpEntity<>(body, headers);
////
////    RestTemplate restTemplate = new RestTemplate();
////    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
////    SmsResponseDTO response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"), httpBody, SmsResponseDTO.class);
////
////    return response;
////  }
//}
