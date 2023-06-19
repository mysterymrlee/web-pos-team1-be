package com.ssg.webpos.service;

import com.ssg.webpos.dto.HQAdminLoginRequestDTO;
import com.ssg.webpos.repository.HQAdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HQAdminServiceTest {
  @Autowired
  HQAdminService hqAdminService;
  @Autowired
  HQAdminRepository hqAdminRepository;

  @Test
  void login() throws UserPrincipalNotFoundException {
    HQAdminLoginRequestDTO hqAdminLoginRequestDTO = new HQAdminLoginRequestDTO();
    hqAdminLoginRequestDTO.setAdminNumber("1234");
    hqAdminLoginRequestDTO.setPassword("0000");
    hqAdminService.login(hqAdminLoginRequestDTO);
  }

}