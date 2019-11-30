package com.moong.envers.team.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@ToString @EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Builder(access = AccessLevel.PROTECTED)
    private Team(String name) {
        this.name = name;
    }

    public static Team newTeam(String name) {
        return Team.builder().name(name).build();
    }
}


