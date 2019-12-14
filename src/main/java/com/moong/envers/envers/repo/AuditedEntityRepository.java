package com.moong.envers.envers.repo;

import com.moong.envers.common.domain.BaseEntity;

import java.util.Optional;

public interface AuditedEntityRepository {

    <T extends BaseEntity> Optional<T> findAuditedEntity(Long revisionNumber, Class<T> entityClass, Object entityId);

}