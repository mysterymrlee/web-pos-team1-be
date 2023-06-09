package com.ssg.webpos.domain.language;

import com.ssg.webpos.dto.TranslationDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "chinese")
@AllArgsConstructor
@NoArgsConstructor
public class CN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String pageName;
    private String one;
    private String two;
    private String three;
    private String four;
    private String five;
    private String six;
    private String seven;
    private String eight;
    private String nine;
    private String ten;
    private String eleven;
    private String twelve;
    private String thirteen;
    public TranslationDTO convertToDTO() {
        return TranslationDTO.builder()
            .pageName(pageName)
            .one(one)
            .two(two)
            .three(three)
            .four(four)
            .five(five)
            .six(six)
            .seven(seven)
            .eight(eight)
            .nine(nine)
            .ten(ten)
            .eleven(eleven)
            .twelve(twelve)
            .build();
    }
}
