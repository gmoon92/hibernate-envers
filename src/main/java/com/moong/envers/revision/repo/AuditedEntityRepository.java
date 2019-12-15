package com.moong.envers.revision.repo;

import com.moong.envers.common.domain.BaseEntity;
import org.hibernate.envers.RevisionType;

import java.util.Optional;

public interface AuditedEntityRepository {

    <T extends BaseEntity> Optional<T> findAuditedEntity(Class<T> entityClass, Object entityId, Long revisionNumber);

    <T extends BaseEntity> Optional<T> findAuditedEntity(Class<T> entityClass, Object entityId, Long revisionNumber, RevisionType revisionType);

    <T extends BaseEntity> Optional<T> findPreAuditedEntity(Class<T> entityClass, Object entityId, Long revisionNumber);
}