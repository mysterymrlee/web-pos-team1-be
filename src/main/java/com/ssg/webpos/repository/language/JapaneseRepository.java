package com.ssg.webpos.repository.language;

import com.ssg.webpos.domain.language.JA;
import com.ssg.webpos.domain.language.KO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JapaneseRepository extends JpaRepository<JA, Long> {
  Optional<JA> findByPageName(String pageName);
}
