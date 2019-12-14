package com.moong.envers.envers.repo;

import com.moong.envers.envers.domain.RevisionHistoryModified;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevisionHistoryModifiedRepository extends JpaRepository<RevisionHistoryModified, Long>, AuditedEntityRepository {

}