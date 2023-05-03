package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartDto;
import org.springframework.data.repository.CrudRepository;

public interface RedisRepository extends CrudRepository<CartDto, String>{

}
