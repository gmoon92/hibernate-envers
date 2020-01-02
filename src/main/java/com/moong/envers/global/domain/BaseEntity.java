package com.moong.envers.global.domain;


import lombok.Getter;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@Getter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

}
