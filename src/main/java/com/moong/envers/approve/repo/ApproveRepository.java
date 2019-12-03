package com.moong.envers.approve.repo;

import com.moong.envers.approve.domain.Approve;
import com.moong.envers.member.domain.Member;
import com.moong.envers.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApproveRepository extends JpaRepository<Approve, Approve.Id>, ApproveRepositoryCustom {
    Approve findByMemberAndTeam(Member approveMember, Team approveTeam);
}
