package com.moong.envers.revision.types;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.applyForm.vo.ApplyFormVO;
import com.moong.envers.approve.domain.Approve;
import com.moong.envers.approve.vo.ApproveVO;
import com.moong.envers.common.domain.BaseEntity;
import com.moong.envers.common.vo.EntityCompareVO;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.vo.MemberCompareVO;
import com.moong.envers.revision.core.utils.RevisionConverter;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.vo.TeamCompareVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Getter
public enum RevisionTarget {

     MEMBER(Member.class, MemberCompareVO.class)
    ,TEAM(Team.class, TeamCompareVO.class)
    ,APPROVE(Approve.class, ApproveVO.class)
    ,APPLY_FORM(ApplyForm.class, ApplyFormVO.class)
    ;

    private final Class<? extends BaseEntity> entityClass;
    private final Class<? extends EntityCompareVO> compareVOClass;

    RevisionTarget(Class<? extends BaseEntity> entityClass, Class<? extends EntityCompareVO> compareVOClass) {
        this.entityClass = entityClass;
        this.compareVOClass = compareVOClass;
    }

    public static RevisionTarget of(Class entityClass) {
        return Arrays.stream(RevisionTarget.values())
                .filter(target -> target.getEntityClass().equals(entityClass))
                .findFirst()
                .get();
    }

    public Object ofCompareVO(Object entity) {
        return RevisionConverter.ofCompareVO(getEntityClass().cast(entity), getCompareVOClass());
    }
}
