package com.ssg.webpos.repository;

import com.ssg.webpos.dto.CartAddDTO;
import org.springframework.data.repository.CrudRepository;

public interface RedisRepository extends CrudRepository<CartAddDTO, String>{

}
