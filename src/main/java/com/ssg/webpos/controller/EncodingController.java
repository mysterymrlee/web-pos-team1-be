package com.ssg.webpos.controller;

import com.ssg.webpos.dto.encode.EncodeDTO;
import com.ssg.webpos.service.EncodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/encoding")
public class EncodingController {
  private final EncodingService encodingService;

  @PostMapping("")
  public ResponseEntity<Void> encodingDeliveryAddress(@RequestBody EncodeDTO encodeDTO) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
    encodingService.saveEncodedDeliveryAddressData(encodeDTO);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
