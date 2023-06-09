package com.ssg.webpos.repository.language;

import com.ssg.webpos.domain.language.CN;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChineseRepository extends JpaRepository<CN, Long> {
  Optional<CN> findByPageName(String pageName);

}
