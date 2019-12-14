package com.moong.envers.envers.repo;

import com.moong.envers.envers.domain.RevisionHistoryModified;
import com.moong.envers.envers.types.RevisionEventStatus;
import com.moong.envers.envers.types.RevisionTarget;
import com.moong.envers.member.domain.Member;
import com.moong.envers.team.domain.Team;

import java.util.List;
import java.util.Optional;

public interface RevisionHistoryModifiedRepositoryCustom {

    void updateRevisionModifiedByTargetDataAndEventStatus(Long revisionModifiedEntityId, Team targetTeam, Member targetMember, RevisionEventStatus eventStatus);

    Optional<RevisionHistoryModified> findPreRevisionHistoryModified(RevisionHistoryModified modified);

    List<RevisionHistoryModified> findAllByRevisionNumberAndTarget(Long revisionNumber, RevisionTarget target);
}
