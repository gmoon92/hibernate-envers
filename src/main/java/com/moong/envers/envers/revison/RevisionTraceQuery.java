package com.moong.envers.envers.revison;

import com.moong.envers.common.domain.BaseEntity;
import com.moong.envers.envers.domain.RevisionHistoryModified;
import com.moong.envers.envers.types.RevisionTarget;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.moong.envers.envers.domain.QRevisionHistory.revisionHistory;
import static com.moong.envers.envers.domain.QRevisionHistoryModified.revisionHistoryModified;

@Slf4j
public class RevisionTraceQuery {

  private final EntityManager em;

  public RevisionTraceQuery(EntityManager em) {
    this.em = em;
  }

  public Object getEntityAud(Long revisionNumber, String entityId, Class<? extends BaseEntity> entityClass) {
    try {
      RevisionTarget target = RevisionTarget.of(entityClass);
      return AuditReaderFactory.get(em)
              .createQuery()
              .forEntitiesModifiedAtRevision(entityClass, revisionNumber)
              .add(AuditEntity.id().eq(target.convertToEntityID(entityId)))
              .getSingleResult();
    } catch (Exception e) {
      log.warn("Not select entity aud... revisionNumber : {}, entityId : {}, target : {}", revisionNumber, entityId, entityClass, e.fillInStackTrace());
      return null;
    }
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
