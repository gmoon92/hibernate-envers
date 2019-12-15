package com.moong.envers.revision.repo;

import com.moong.envers.revision.domain.RevisionHistoryModified;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevisionHistoryModifiedRepository extends JpaRepository<RevisionHistoryModified, Long>
        , RevisionHistoryModifiedRepositoryCustom
        , AuditedEntityRepository {

}