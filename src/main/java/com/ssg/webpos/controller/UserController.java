package com.ssg.webpos.controller;

import com.ssg.webpos.domain.User;
import com.ssg.webpos.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("")
    public ResponseEntity getUserList() {
        List<User> findUserList = userRepository.findAll();

        for (User user : findUserList) {
            log.info("User = " + user);
        }


        Optional<User> optFindUser = userRepository.findByPhoneNumber("01011112222");
        if (optFindUser.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        User findUser = optFindUser.get();
        log.info("findUser = " + findUser);

        return new ResponseEntity(HttpStatus.OK);
    }
}
