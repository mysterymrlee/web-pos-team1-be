package com.ssg.webpos.service;

import com.ssg.webpos.domain.User;
import com.ssg.webpos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  public boolean checkMemberExist(String phoneNumber) {
    Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
    if (userOptional.isPresent()) {
      return true;
    }

    return false;
  }
}
