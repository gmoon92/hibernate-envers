package com.moong.envers.applyForm.repo;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.approve.types.ApproveStatus;
import com.moong.envers.common.config.BaseJPARepositoryTestCase;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.repo.TeamRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class ApplyFormRepositoryTest extends BaseJPARepositoryTestCase {

    private final ApplyFormRepository applyFormRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    @Test
    @DisplayName("신청서 저장 테스트")
    void testApplyFormSave() {
        Team applyTeam = teamRepository.save(Team.newTeam("web1"));
        Member applyMember = memberRepository.save(Member.builder().name("moon").build());

        ApplyForm applyForm = applyFormRepository.save(ApplyForm.write(applyMember, applyTeam, "신청 테스트입니다."));
        Assertions.assertThat(applyForm.getStatus()).isEqualTo(ApproveStatus.WAIT);
    }
}