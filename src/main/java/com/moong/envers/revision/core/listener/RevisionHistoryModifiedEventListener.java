package com.moong.envers.revision.core.listener;

import com.moong.envers.member.domain.Member;
import com.moong.envers.revision.core.exception.RevisionHistoryException;
import com.moong.envers.revision.core.utils.RevisionConverter;
import com.moong.envers.revision.domain.RevisionHistoryModified;
import com.moong.envers.revision.repo.AuditedEntityRepository;
import com.moong.envers.revision.repo.AuditedEntityRepositoryImpl;
import com.moong.envers.revision.repo.RevisionHistoryModifiedRepositoryCustom;
import com.moong.envers.revision.repo.RevisionHistoryModifiedRepositoryImpl;
import com.moong.envers.revision.types.RevisionEventStatus;
import com.moong.envers.revision.types.RevisionTarget;
import com.moong.envers.team.domain.Team;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Optional;

import static com.moong.envers.revision.types.RevisionEventStatus.DISPLAY;
import static com.moong.envers.revision.types.RevisionEventStatus.ERROR;
import static com.moong.envers.revision.types.RevisionEventStatus.NOT_DISPLAY;

@Slf4j
public class RevisionHistoryModifiedEventListener implements PostInsertEventListener {

    private final EntityManager em;

    private AuditedEntityRepository auditedEntityRepository;

    private RevisionHistoryModifiedRepositoryCustom revisionHistoryModifiedRepository;

    public RevisionHistoryModifiedEventListener(EntityManager em) {
        this.em = em;
        this.auditedEntityRepository = new AuditedEntityRepositoryImpl(em);
        this.revisionHistoryModifiedRepository = new RevisionHistoryModifiedRepositoryImpl(em);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof RevisionHistoryModified) {
            log.info("PostInsertEventListener start...");

            RevisionHistoryModified modified = RevisionHistoryModified.class.cast(entity);

            Object auditedEntity = getAuditedEntity(modified).get();
            Optional<RevisionEventStatus> eventStatus = Optional.empty();
            try {
                Optional<Object> maybePreAuditedEntity = getPreAuditedEntity(modified);
                eventStatus = getRevisionEventStatus(modified.getRevisionTarget(), auditedEntity, maybePreAuditedEntity);
            } catch (RevisionHistoryException ex) {
                log.warn("Unexpected exception... ", ex);
                eventStatus = Optional.of(ERROR);;
            } finally {
                updateRevisionModifiedEntity(modified, auditedEntity, eventStatus.orElse(NOT_DISPLAY));
            }
        }
    }

    private Optional<Object> getAuditedEntity(RevisionHistoryModified modified) {
        RevisionTarget target = modified.getRevisionTarget();

        Long revisionNumber = modified.getRevision().getId();
        Class entityClass = target.getEntityClass();
        Object entityId = RevisionConverter.deSerializedObject(modified.getEntityId());
        return auditedEntityRepository.findAuditedEntity(entityClass, entityId, revisionNumber);
    }

    private Optional<Object> getPreAuditedEntity(RevisionHistoryModified modified) {
//        return revisionHistoryModifiedRepository.findPreRevisionHistoryModified(modified)
//            .flatMap(this::getAuditedEntity);
        RevisionTarget target = modified.getRevisionTarget();

        Long revisionNumber = modified.getRevision().getId();
        Class entityClass = target.getEntityClass();
        Object entityId = RevisionConverter.deSerializedObject(modified.getEntityId());
        return auditedEntityRepository.findPreAuditedEntity(entityClass, entityId, revisionNumber);
    }

    private Optional<RevisionEventStatus> getRevisionEventStatus(RevisionTarget target, Object auditedEntity, Optional<Object> maybePreAuditedEntity) {
        if (maybePreAuditedEntity.isPresent()
                && compareToEntityVO(target, auditedEntity, maybePreAuditedEntity.get())) {
            return Optional.of(NOT_DISPLAY);
        } else {
            return Optional.of(DISPLAY);
        }
    }

    private boolean compareToEntityVO(RevisionTarget target, Object auditedEntity, Object preAuditedEntity) {
        Object currentVO  = target.ofCompareVO(auditedEntity);
        Object previousVO = target.ofCompareVO(preAuditedEntity);
        return currentVO.equals(previousVO);
    }

    private void updateRevisionModifiedEntity(RevisionHistoryModified modified, Object auditedEntity, RevisionEventStatus eventStatus) {
        EntityTransaction entityTransaction = em.getTransaction();

        Long id = modified.getId();
        RevisionTarget target = modified.getRevisionTarget();
        byte[] entityId = modified.getEntityId();

        Team targetTeam = getTargetTeam(target, auditedEntity, entityId);
        Member targetMember = getTargetMember(target, auditedEntity, entityId);

        try {
            entityTransaction.begin();
            revisionHistoryModifiedRepository.updateTargetDataAndEventStatus(id, targetTeam, targetMember, eventStatus);
            entityTransaction.commit();
        } catch (Exception ex) {
            String errorMessage = String.format("[Error] Update RevisionModifiedEntity id : %s, target : %s, entityId : %s", id, eventStatus);
            log.warn(errorMessage, ex);
            entityTransaction.rollback();
        }
    }

    private Member getTargetMember(RevisionTarget target, Object entity, byte[] entityId) {
        switch (target) {
            case MEMBER:
                return Member.class.cast(entity);
            default:
                return null;
        }
    }

    private Team getTargetTeam(RevisionTarget target, Object entity, byte[] entityId) {
        switch (target) {
            case TEAM:
                return Team.class.cast(entity);
            default:
                return null;
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return true;
    }
}
