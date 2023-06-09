package com.ssg.webpos.controller;

import com.ssg.webpos.domain.language.CN;
import com.ssg.webpos.domain.language.EN;
import com.ssg.webpos.domain.language.JA;
import com.ssg.webpos.domain.language.KO;
import com.ssg.webpos.dto.TranslationDTO;
import com.ssg.webpos.repository.language.ChineseRepository;
import com.ssg.webpos.repository.language.EnglishRepository;
import com.ssg.webpos.repository.language.JapaneseRepository;
import com.ssg.webpos.repository.language.KoreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/translation")
public class TranslationController {
  @Autowired
  private EnglishRepository englishRepository;
  @Autowired
  private ChineseRepository chineseRepository;
  @Autowired
  private JapaneseRepository japaneseRepository;

  @Autowired
  private KoreaRepository koreaRepository;

  @GetMapping("/{service}/{pageName}")
  public TranslationDTO getTranslation(
      @PathVariable("service") int service,
      @PathVariable("pageName") String pageName) {

    if (service == 0) { // Korea
      TranslationDTO translationDTO = koreaRepository.findByPageName(pageName).map(
          ko -> ko.convertToDTO()).get();
      return translationDTO;
    } else if (service == 1) { // English
      TranslationDTO translationDTO = englishRepository.findByPageName(pageName).map(
          en -> en.convertToDTO()).get();
      return translationDTO;
    } else if (service == 2) { // Japanese
      TranslationDTO translationDTO = japaneseRepository.findByPageName(pageName).map(
          ja -> ja.convertToDTO()).get();
      return translationDTO;
    } else if (service == 3) { // Chinese
      TranslationDTO translationDTO = chineseRepository.findByPageName(pageName).map(
          cn -> cn.convertToDTO()).get();
      return translationDTO;
    } else {
      throw new IllegalArgumentException("잘못된 서비스 번호");
    }
  }
}