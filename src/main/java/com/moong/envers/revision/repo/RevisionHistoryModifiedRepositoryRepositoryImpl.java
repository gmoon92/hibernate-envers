package com.moong.envers.revision.repo;

import com.moong.envers.member.domain.Member;
import com.moong.envers.revision.domain.RevisionHistoryModified;
import com.moong.envers.revision.types.RevisionEventStatus;
import com.moong.envers.revision.types.RevisionTarget;
import com.moong.envers.team.domain.Team;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.moong.envers.envers.domain.QRevisionHistory.revisionHistory;
import static com.moong.envers.envers.domain.QRevisionHistoryModified.revisionHistoryModified;

@RequiredArgsConstructor
public class RevisionHistoryModifiedRepositoryRepositoryImpl implements RevisionHistoryModifiedRepositoryCustom {

    private final EntityManager em;

    @Override
    @Transactional
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
    public List<RevisionHistoryModified> findAllByRevisionNumberAndTarget(Long revisionNumber, RevisionTarget target) {
        JPAQuery<RevisionHistoryModified> query = new JPAQuery(em);
        return query.select(revisionHistoryModified)
                .from(revisionHistoryModified)
                .where(revisionHistoryModified.revision.id.eq(revisionNumber)
                        .and(revisionHistoryModified.revisionTarget.eq(target)))
                .fetch();
    }
}
