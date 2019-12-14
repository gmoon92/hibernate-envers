package com.moong.envers.envers.repo;

import com.moong.envers.common.domain.BaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQueryCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AuditedEntityRepositoryImpl implements AuditedEntityRepository{

    private final EntityManager em;

    protected AuditReader getAuditReader() {
        return AuditReaderFactory.get(em);
    }

    protected AuditQueryCreator getAuditQuery() {
        return getAuditReader().createQuery();
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends BaseEntity> Optional<T> findAuditedEntity(Long revisionNumber, Class<T> entityClass, Object entityId) {
        try {
            Object entity = getAuditQuery()
                    .forEntitiesModifiedAtRevision(entityClass, revisionNumber)
                    .add(AuditEntity.id().eq(entityId))
                    .getSingleResult();

            return Optional.ofNullable(entityClass.cast(entity));
        } catch (NoResultException ex) {
            log.warn("Not found audited data... \n revisionNumber : {}, entityClass : {}, entityId : {}", revisionNumber, entityClass, entityId);
            return Optional.empty();
        } catch (Exception ex) {
            String errorMessage = String.format("Unexpected exception... revisionNumber : %s, entityClass : %s, entityId : %s", revisionNumber, entityClass, entityId);
            log.error(errorMessage, ex);
            throw new RuntimeException(errorMessage, ex);
        }
    }

}
