package com.moong.envers.envers.types;

import com.moong.envers.member.domain.Member;
import com.moong.envers.team.domain.Team;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Function;

@Getter
public enum RevisionTarget {

     MEMBER(Member.class, String::valueOf, Long::valueOf)
    ,TEAM(Team.class, String::valueOf, Long::valueOf);

    private final Class<?> entityClass;
    private final Function<Serializable, String> convertToRevisionEntityIDExpression;
    private final Function<String, Object> convertToEntityIDExpression;

    RevisionTarget(Class<?> entityClass, Function<Serializable, String> convertToRevisionEntityIDExpression, Function<String, Object> convertToEntityIDExpression) {
        this.entityClass = entityClass;
        this.convertToRevisionEntityIDExpression = convertToRevisionEntityIDExpression;
        this.convertToEntityIDExpression = convertToEntityIDExpression;
    }

    public String convertToRevisionEntityID(Serializable entityId) {
       return convertToRevisionEntityIDExpression.apply(entityId);
    }

    public Object convertToEntityID(String revEntityId) {
        return convertToEntityIDExpression.apply(revEntityId);
    }

    public static RevisionTarget of(Class entityClass) {
        return Arrays.stream(RevisionTarget.values())
                .filter(target -> target.getEntityClass().equals(entityClass))
                .findFirst()
                .orElse(null);
    }
}
