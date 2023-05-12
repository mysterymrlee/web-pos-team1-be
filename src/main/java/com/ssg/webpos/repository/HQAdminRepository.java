package com.ssg.webpos.repository;

import com.ssg.webpos.domain.HQAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HQAdminRepository extends JpaRepository<HQAdmin, Long> {

    Optional<HQAdmin> findByAdminNumber(String adminNumber);
}
