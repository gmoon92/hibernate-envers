package com.moong.envers.member.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
@ToString @EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String password;

    private Integer age;

    @Builder
    private Member(String name, String password, Integer age) {
        this.name = name;
        this.password = password;
        this.age = age;
    }
}
