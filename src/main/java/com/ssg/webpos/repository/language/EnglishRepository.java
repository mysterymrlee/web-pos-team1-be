package com.ssg.webpos.repository.language;

import com.ssg.webpos.domain.language.EN;
import com.ssg.webpos.domain.language.KO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnglishRepository extends JpaRepository<EN, Long> {
  Optional<EN> findByPageName(String pageName);
}
