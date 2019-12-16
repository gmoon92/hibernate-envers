package com.moong.envers.revision.repo;

import com.moong.envers.member.domain.Member;
import com.moong.envers.revision.domain.RevisionHistoryModified;
import com.moong.envers.revision.types.RevisionEventStatus;
import com.moong.envers.revision.types.RevisionTarget;
import com.moong.envers.revision.vo.RevisionListVO;
import com.moong.envers.team.domain.Team;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface RevisionHistoryModifiedRepositoryCustom {

    void updateTargetDataAndEventStatus(Long revisionModifiedEntityId, Team targetTeam, Member targetMember, RevisionEventStatus eventStatus);

    Optional<RevisionHistoryModified> findPreRevisionHistoryModified(RevisionHistoryModified modified);

    List<RevisionHistoryModified> findAllByRevisionAndRevisionTarget(Long revisionNumber, RevisionTarget target);

    Page<RevisionListVO.DataVO> findAllBySearchVO(RevisionListVO.SearchVO searchVO);
}
