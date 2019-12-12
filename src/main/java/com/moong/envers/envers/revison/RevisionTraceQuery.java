package com.moong.envers.envers.revison;

import com.moong.envers.common.domain.BaseEntity;
import com.moong.envers.envers.config.exception.RevisionException;
import com.moong.envers.envers.domain.RevisionHistoryModified;
import com.moong.envers.envers.types.RevisionTarget;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQueryCreator;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.moong.envers.envers.domain.QRevisionHistory.revisionHistory;
import static com.moong.envers.envers.domain.QRevisionHistoryModified.revisionHistoryModified;

@Slf4j
@Component
@RequiredArgsConstructor
public class RevisionTraceQuery {

    private final EntityManager em;

    public Object getEntityAud(Long revNumber, String entityId, Class<? extends BaseEntity> entityClass) {
        try {
            RevisionTarget target = RevisionTarget.of(entityClass);
            return getAuditQuery()
                    .forEntitiesModifiedAtRevision(entityClass, revNumber)
                    .add(AuditEntity.id().eq(target.convertToEntityID(entityId)))
                    .getSingleResult();
        } catch (Exception ex) {
            throw new RevisionException(ex, "Not found revision... Revision Number : %s, entityClass : %s, entityId : %s", revNumber, entityClass, entityId);
        }
    }

    private AuditReader getAuditReader() {
        return AuditReaderFactory.get(em);
    }

    public AuditQueryCreator getAuditQuery() {
        return getAuditReader().createQuery();
    }

    public Optional<RevisionHistoryModified> getPreModifiedRevision(RevisionHistoryModified revision) {
        JPAQuery<RevisionHistoryModified> query = new JPAQuery(em);

        query.select(revisionHistoryModified)
                .from(revisionHistory)
                .innerJoin(revisionHistory.modifiedEntities, revisionHistoryModified)
                .where(revisionHistoryModified.revision.id.eq(JPAExpressions.select(revisionHistory.id.max())
                        .from(revisionHistory)
                        .innerJoin(revisionHistory.modifiedEntities, revisionHistoryModified)
                        .where(revisionHistoryModified.entityId.eq(revision.getEntityId())
                                .and(revisionHistoryModified.revisionTarget.eq(revision.getRevisionTarget()))
                                .and(revisionHistoryModified.revision.id.lt(revision.getRevision().getId())))));
        return Optional.ofNullable(query.fetchOne());
    }

    public List<RevisionHistoryModified> getModifiedEntities(Long revisionNumber, RevisionTarget target) {
        JPAQuery<RevisionHistoryModified> query = new JPAQuery(em);
        return query.select(revisionHistoryModified)
                .from(revisionHistoryModified)
                .where(revisionHistoryModified.revision.id.eq(revisionNumber)
                        .and(revisionHistoryModified.revisionTarget.eq(target)))
                .fetch();
    }
}
