package com.ssg.webpos.repository;

import com.ssg.webpos.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByPhoneNumber(String phoneNumber);
//  Optional<User> findByUserId(Long userId);

}
