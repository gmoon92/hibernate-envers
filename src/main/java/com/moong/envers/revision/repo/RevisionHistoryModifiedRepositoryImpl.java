package com.moong.envers.revision.repo;

import com.moong.envers.member.domain.Member;
import com.moong.envers.revision.domain.RevisionHistoryModified;
import com.moong.envers.revision.types.RevisionEventStatus;
import com.moong.envers.revision.types.RevisionTarget;
import com.moong.envers.revision.vo.QRevisionListVO_DataVO;
import com.moong.envers.revision.vo.RevisionListVO;
import com.moong.envers.team.domain.Team;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.RevisionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.moong.envers.revision.domain.QRevisionHistory.revisionHistory;
import static com.moong.envers.revision.domain.QRevisionHistoryModified.revisionHistoryModified;

public class RevisionHistoryModifiedRepositoryImpl extends QuerydslRepositorySupport implements RevisionHistoryModifiedRepositoryCustom {

    private final EntityManager em;

    public RevisionHistoryModifiedRepositoryImpl(EntityManager em) {
        super(RevisionHistoryModified.class);
        this.em = em;
    }

    @Override
    public void updateTargetDataAndEventStatus(Long modifiedId, Team targetTeam, Member targetMember, RevisionEventStatus eventStatus) {
        new JPAUpdateClause(em, revisionHistoryModified)
                .set(revisionHistoryModified.revisionEventStatus, eventStatus)
                .where(revisionHistoryModified.id.eq(modifiedId))
                .execute();
    }

    @Override
    public Optional<RevisionHistoryModified> findPreRevisionHistoryModified(RevisionHistoryModified modified) {
        JPAQuery<RevisionHistoryModified> query = new JPAQuery(em);
        query.select(revisionHistoryModified)
                .from(revisionHistory)
                .innerJoin(revisionHistory.modifiedEntities, revisionHistoryModified)
                .where(revisionHistoryModified.revision.id.eq(JPAExpressions.select(revisionHistory.id.max())
                        .from(revisionHistory)
                        .innerJoin(revisionHistory.modifiedEntities, revisionHistoryModified)
                        .where(revisionHistoryModified.entityId.eq(modified.getEntityId())
                                .and(revisionHistoryModified.revisionTarget.eq(modified.getRevisionTarget()))
                                .and(revisionHistoryModified.revision.id.lt(modified.getRevision().getId())))));
        return Optional.ofNullable(query.fetchOne());
    }

    @Override
    public List<RevisionHistoryModified> findAllByRevisionAndRevisionTarget(Long revisionNumber, RevisionTarget target) {
        JPAQuery<RevisionHistoryModified> query = new JPAQuery(em);
        return query.select(revisionHistoryModified)
                .from(revisionHistoryModified)
                .where(revisionHistoryModified.revision.id.eq(revisionNumber)
                        .and(revisionHistoryModified.revisionTarget.eq(target)))
                .fetch();
    }

    @Override
    public Page<RevisionListVO.DataVO> findAllBySearchVO(RevisionListVO.SearchVO searchVO) {
        JPAQuery<RevisionListVO.DataVO> query = new JPAQuery(em);

        Pageable pageable = searchVO.getPageable();
        query.select(new QRevisionListVO_DataVO(revisionHistory.id, revisionHistory.createdDt
                , revisionHistory.updatedBy, revisionHistory.updatedByUsername
                , revisionHistoryModified.revisionTarget, revisionHistoryModified.entityId
                , revisionHistoryModified.targetTeamName, revisionHistoryModified.targetMemberName))
                .from(revisionHistory)
                .innerJoin(revisionHistory.modifiedEntities, revisionHistoryModified)
                .where(revisionHistoryModified.revisionEventStatus.eq(RevisionEventStatus.DISPLAY))
                .where(revisionHistoryModified.revisionType.eq(RevisionType.MOD));

        setSearchCondition(query, searchVO);

        List<RevisionListVO.DataVO> list = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl(list, pageable, query.fetchCount());
    }

    private void setSearchCondition(JPAQuery query, RevisionListVO.SearchVO search) {
        Date startDt = Optional.ofNullable(search.getStartDt()).orElse(new Date());
        Date endDt = Optional.ofNullable(search.getEndDt()).orElse(new Date());

        query.where(revisionHistory.createdDt.between(startDt, endDt));

        RevisionListVO.SearchVO.SearchType keywordCondition = Optional.ofNullable(search.getSearchType())
                .orElse(RevisionListVO.SearchVO.SearchType.EMPTY);

        String searchKeyword = search.getSearchKeyword();

        if (StringUtils.isNotBlank(searchKeyword)) {
            switch (keywordCondition) {
                case MEMBER_NAME:
                    query.where(revisionHistory.updatedByUsername.contains(searchKeyword));
                    break;
                case TARGET_MEMBER_NAME:
                    query.where(revisionHistoryModified.targetMemberName.contains(searchKeyword));
                    break;
                case TARGET_TEAM_NAME:
                    query.where(revisionHistoryModified.targetTeamName.contains(searchKeyword));
                    break;
            }
        }
    }
}
