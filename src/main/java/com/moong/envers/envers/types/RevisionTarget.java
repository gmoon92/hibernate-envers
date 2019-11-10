package com.moong.envers.envers.types;

import com.moong.envers.member.domain.Member;
import com.moong.envers.team.domain.Team;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Function;

@Getter
public enum RevisionTarget {

     MEMBER(Member.class, String::valueOf)
    ,TEAM(Team.class, String::valueOf);

    private final Class<?> entityClass;
    private final Function<Serializable, String> convertToRevisionEntityIDExpression;

    RevisionTarget(Class<?> entityClass, Function<Serializable, String> convertToRevisionEntityIDExpression) {
        this.entityClass = entityClass;
        this.convertToRevisionEntityIDExpression = convertToRevisionEntityIDExpression;
    }

    public String convertToRevisionEntityID(Serializable entityId) {
       return convertToRevisionEntityIDExpression.apply(entityId);
    }

    public static RevisionTarget of(Class entityClass) {
        return Arrays.stream(RevisionTarget.values())
                .filter(target -> target.getEntityClass().equals(entityClass))
                .findFirst()
                .orElse(null);
    }
}
