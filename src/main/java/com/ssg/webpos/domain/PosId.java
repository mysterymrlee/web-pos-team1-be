package com.ssg.webpos.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;
@Embeddable
public class PosId implements Serializable {
    private Long id;
    private Long store;
}
