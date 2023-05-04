package com.ssg.webpos.repository.pos;

import com.ssg.webpos.domain.Pos;
import com.ssg.webpos.domain.PosId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosRepository extends JpaRepository<Pos, PosId> {

}