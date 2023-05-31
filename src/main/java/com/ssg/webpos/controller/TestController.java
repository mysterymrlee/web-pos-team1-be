package com.ssg.webpos.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/ping")
@Slf4j
@RequiredArgsConstructor
public class TestController {
  @GetMapping("")
  public ResponseEntity test() {
    String pong = "pong";
    return new ResponseEntity(pong, HttpStatus.OK);
  }
}
