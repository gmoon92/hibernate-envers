package com.moong.envers.applyForm.service;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.applyForm.repo.ApplyFormRepository;
import com.moong.envers.approve.domain.Approve;
import com.moong.envers.approve.repo.ApproveRepository;
import com.moong.envers.common.config.BaseJPARepositoryTestCase;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.repo.TeamRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class ApplyFormServiceTest extends BaseJPARepositoryTestCase {

    private final ApplyFormRepository applyFormRepository;
    private final ApproveRepository approveRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    @BeforeEach
    void init() {
        Team web1 = Team.newTeam("web1");
        Team web2 = Team.newTeam("web2");
        teamRepository.saveAll(Arrays.asList(web1, web2));

        Member kim = Member.builder().name("kim").team(web1).build();
        Member lee = Member.builder().name("lee").team(web1).build();
        Member moon = Member.builder().name("moon").team(web1).build();
        Member jts = Member.builder().name("jts").team(web2).build();

        Member newcomer1 = Member.builder().name("newcomer1").build();
        Member newcomer2 = Member.builder().name("newcomer2").build();
        memberRepository.saveAll(Arrays.asList(kim, lee, moon, jts, newcomer1, newcomer2));

        approveRepository.saveAll(Arrays.asList(
                 Approve.register(kim, web1)
                ,Approve.register(lee, web1)
        ));
        doEntityManagerFlushAndClear();
        log.info("\n\n\n\n");
    }

    @Test
    @DisplayName("신청서의 대한 승인자 조회")
    void testRetrieveApproveAboutApplyForm() {
        testWriteApplyForm();
        doEntityManagerFlushAndClear();
        log.info(">>>>>");


    }

    @Test
    @DisplayName("신청서 저장")
    void testWriteApplyForm() {
        Optional<Member> member1 = memberRepository.findByName("newcomer1");
        Optional<Member> member2 = memberRepository.findByName("newcomer2");
        writeForApplyFormByAllTeam(member1);
        writeForApplyFormByAllTeam(member2);
    }

    private void writeForApplyFormByAllTeam(Optional<Member> member) {
        member.ifPresent(applyMember -> {
            List<Team> teams = teamRepository.findAll();
            for (Team applyTeam : teams) {
                applyFormRepository.save(ApplyForm.write(applyMember, applyTeam,  String.format("%s 부서에 신청합니다.", applyTeam.getName())));
            }
        });
    }

    @Test
    @DisplayName("신청서 승인 상태 변경")
    void testChangeApplyFormStatus() {
        testWriteApplyForm();

        Optional<Member> member = memberRepository.findByName("kim");
        log.info("approve member -> {}", member);
        member.ifPresent(approveMember -> {
            List<ApplyForm> applyForms = applyFormRepository.findApplyFormsToApprove(approveMember);
            log.info("retrieve is apply form -> {}", applyForms);
        });
    }
}