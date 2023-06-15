package com.ssg.webpos.repository.store;

import com.ssg.webpos.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findAllByOrderByName();
}
