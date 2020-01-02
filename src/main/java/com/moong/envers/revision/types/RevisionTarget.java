package com.moong.envers.revision.types;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.approve.domain.Approve;
import com.moong.envers.global.domain.BaseTrackingEntity;
import com.moong.envers.global.vo.EntityCompareVO;
import com.moong.envers.member.domain.Member;
import com.moong.envers.revision.utils.RevisionConverter;
import com.moong.envers.revision.vo.compare.ApplyFormCompareVO;
import com.moong.envers.revision.vo.compare.ApproveCompareVO;
import com.moong.envers.revision.vo.compare.MemberCompareVO;
import com.moong.envers.revision.vo.compare.TeamCompareVO;
import com.moong.envers.team.domain.Team;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum RevisionTarget {

     MEMBER(Member.class, MemberCompareVO.class)
    ,TEAM(Team.class, TeamCompareVO.class)
    ,APPROVE(Approve.class, ApproveCompareVO.class)
    ,APPLY_FORM(ApplyForm.class, ApplyFormCompareVO.class)
    ;

    private final Class<? extends BaseTrackingEntity> entityClass;
    private final Class<? extends EntityCompareVO> compareVOClass;

    public static RevisionTarget of(Class entityClass) {
        return Arrays.stream(RevisionTarget.values())
                .filter(target -> target.getEntityClass().equals(entityClass))
                .findFirst()
                .get();
    }

    public Object getCompareVO(Object entity) {
        return RevisionConverter.convertTo(getEntityClass().cast(entity), getCompareVOClass());
    }
}
