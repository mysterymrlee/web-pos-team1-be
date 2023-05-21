package com.ssg.webpos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.webpos.dto.msg.MessageDTO;
import com.ssg.webpos.dto.msg.SmsResponseDTO;
import com.ssg.webpos.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/sms")
public class SmsController {
  @Autowired
  SmsService smsService;

  @PostMapping("/send")
  public SmsResponseDTO sendSms(@RequestBody MessageDTO messageDTO) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    SmsResponseDTO responseDTO = smsService.sendSms(messageDTO);
    return responseDTO;
  }
}
