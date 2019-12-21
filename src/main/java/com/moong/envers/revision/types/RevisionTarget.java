package com.moong.envers.revision.types;

import com.moong.envers.common.domain.BaseEntity;
import com.moong.envers.common.vo.EntityCompareVO;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.vo.MemberCompareVO;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.vo.TeamCompareVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Function;

@Slf4j
@Getter
public enum RevisionTarget {

     MEMBER(Member.class
             , MemberCompareVO.class
             , String::valueOf
             , Long::valueOf)
    ,TEAM(Team.class
            , TeamCompareVO.class
            , String::valueOf
            , Long::valueOf);

    private final Class<? extends BaseEntity> entityClass;
    private final Class<? extends EntityCompareVO> compareVOClass;
    private final Function<Serializable, String> convertToRevisionEntityIDExpression;
    private final Function<String, Object> convertToEntityIDExpression;

    RevisionTarget(Class<? extends BaseEntity> entityClass, Class<? extends EntityCompareVO> compareVOClass, Function<Serializable, String> convertToRevisionEntityIDExpression, Function<String, Object> convertToEntityIDExpression) {
        this.entityClass = entityClass;
        this.compareVOClass = compareVOClass;
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

    public Object newInstanceCompareVO(Object entity) {
        Object compareVO = null;
        try {
            compareVO = getCompareVOClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("{}", e);
            throw new RuntimeException("Not convert to compare value object...", e);
        }

        BeanUtils.copyProperties(entity, compareVO);
        return compareVO;
    }

    public static <T extends BaseEntity, R extends EntityCompareVO> R newInstanceCompareVO(T entity, Class<R> compareVOClass) {
        Object compareVO = null;
        try {
            compareVO = compareVOClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("{}", e);
            throw new RuntimeException("Not convert to compare value object...", e);
        }

        BeanUtils.copyProperties(entity, compareVO);
        return (R) compareVO;
    }
}
