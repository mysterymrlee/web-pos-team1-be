package com.ssg.webpos.repository.pos;

import com.ssg.webpos.domain.Pos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosRepository extends JpaRepository<Pos, Long> {

}