package com.ssg.webpos.domain.language;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "korean")
@AllArgsConstructor
@NoArgsConstructor
public class KO {
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
}
