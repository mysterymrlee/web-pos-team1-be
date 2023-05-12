package com.ssg.webpos.repository;

import com.ssg.webpos.domain.BranchAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchAdminRepository extends JpaRepository<BranchAdmin, Long> {
    Optional<BranchAdmin> findByAdminNumber(String adminNumber);
}
