package com.moong.envers.envers.config;

import com.moong.envers.envers.domain.RevisionHistoryModified;
import com.moong.envers.envers.revison.RevisionTraceQuery;
import com.moong.envers.envers.types.RevisionEventStatus;
import com.moong.envers.envers.types.RevisionTarget;
import com.moong.envers.member.domain.Member;
import com.moong.envers.team.domain.Team;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Optional;

import static com.moong.envers.envers.domain.QRevisionHistoryModified.revisionHistoryModified;
import static com.moong.envers.envers.types.RevisionEventStatus.ERROR;
import static com.moong.envers.envers.types.RevisionEventStatus.NOT_SUITABLE;
import static com.moong.envers.envers.types.RevisionEventStatus.SUITABLE;

@Slf4j
public class RevisionHistoryModifiedEventListener implements PostInsertEventListener {

    private final EntityManager em;
    private final RevisionTraceQuery traceQuery;

    public RevisionHistoryModifiedEventListener(EntityManager em) {
        this.em = em;
        this.traceQuery = new RevisionTraceQuery(em);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof RevisionHistoryModified) {
            log.info("PostInsertEventListener start...");

//            EntityManager em = event.getSession().getEntityManagerFactory().createEntityManager();
            RevisionHistoryModified revModified = RevisionHistoryModified.class.cast(entity);

            Long revNumber = revModified.getRevision().getId();
            Long revModifiedId = revModified.getId();

            RevisionTarget revTarget = revModified.getRevisionTarget();
            Class entityClass = revTarget.getEntityClass();
            String entityId = revModified.getEntityId();

            RevisionEventStatus eventStatus = SUITABLE;
            String targetUserId = null;
            String targetAgentId = null;

            try {
                Object currentEntity = traceQuery.getEntityAud(revNumber, entityId, entityClass);

                Optional<RevisionHistoryModified> beforeRevisionModified = traceQuery.getPreModifiedRevision(revModified);

                if (beforeRevisionModified.isPresent()) {
                    RevisionHistoryModified before = beforeRevisionModified.get();
                    Long beforeRevisionNumber = before.getRevision().getId();
                    String beforeEntityId = before.getEntityId();

                    Object beforeEntity = traceQuery.getEntityAud(beforeRevisionNumber, beforeEntityId, entityClass);

                    if (isCompareToEntity(currentEntity, beforeEntity, revTarget)) {
                        eventStatus = NOT_SUITABLE;
                    }
                }
                targetUserId = String.valueOf(getTargetUserId(em, currentEntity, entityId, revTarget));
                targetAgentId = String.valueOf(getTargetTeamId(em, currentEntity, entityId, revTarget));
            } catch (Exception ex) {
                eventStatus = ERROR;
                log.error("[ERR] onPostInsert occur", ex);
            } finally {
                updateRevisionModifiedEntity(em, revModifiedId, eventStatus, targetAgentId, targetUserId);
            }
        }
    }

    private Object getTargetUserId(EntityManager em, Object entity, String entityId, RevisionTarget target) {
        switch (target) {
            case MEMBER:
                return Member.class.cast(entity).getId();
            default:
                return null;
        }
    }

    private Object getTargetTeamId(EntityManager em, Object entity, String entityId, RevisionTarget target) {
        switch (target) {
            case TEAM:
                return Team.class.cast(entity).getId();
            default:
                return null;
        }
    }

    private void updateRevisionModifiedEntity(EntityManager em, Long revisionModifiedEntityId, RevisionEventStatus eventStatus, String targetAgentId, String targetUserId) {
        log.debug("Update HistoryRevisionModifiedEntity... id : {}, eventStatus : {}", revisionModifiedEntityId, eventStatus);
        EntityTransaction entityTransaction = em.getTransaction();
        try {
            entityTransaction.begin();
            new JPAUpdateClause(em, revisionHistoryModified)
                    .set(revisionHistoryModified.revisionEventStatus, eventStatus)
//                    .set(revisionHistoryModified.targetAgentId, targetAgentId)
//                    .set(revisionHistoryModified.targetUserId, targetUserId)
                    .where(revisionHistoryModified.id.eq(revisionModifiedEntityId))
                    .execute();
            entityTransaction.commit();
        } catch (Exception e) {
            log.warn("[Error] HistoryRevisionEventListener.updateRevisionModifiedEntity Update HistoryRevisionModifiedEntity id : {}, eventStatus : {}", revisionModifiedEntityId, eventStatus, e.fillInStackTrace());
            entityTransaction.rollback();
        }
    }

    private boolean isCompareToEntity(Object currentEntity, Object beforeEntity, RevisionTarget target) {
//        Object currentVO = target.convertVO(currentEntity);
//        Object beforeVO = target.convertVO(beforeEntity);
//        return currentVO.equals(beforeVO);
        return true;
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return true;
    }
}
