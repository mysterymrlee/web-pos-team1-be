package com.ssg.webpos.repository.language;

import com.ssg.webpos.domain.language.KO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KoreaRepository extends JpaRepository<KO, Long> {
  Optional<KO> findByPageName(String pageName);
}
