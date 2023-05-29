package com.ssg.webpos.repository;

import com.itextpdf.text.pdf.parser.clipper.Point;
import com.ssg.webpos.domain.PointUseHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;
import java.util.Optional;

@SpringBootTest
public class PointUseHistoryRepositoryTest {
    @Autowired
    PointUseHistoryRepository pointUseHistoryRepository;
    @Test
    void contextVoid() {
        Optional<PointUseHistory> p =pointUseHistoryRepository.findByOrderId(89L);
        int t = p.get().getAmount();
        System.out.println(t);
    }

}
