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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
                ,Approve.register(jts, web2)
        ));
        doEntityManagerFlushAndClear();
        log.info("\n\n\n\n");
    }

    @Test
    @DisplayName("신청서에 대한 승인자 조회")
    void testRetrieveApproverAboutApplyForm() {
        Optional<Member> maybeMember = memberRepository.findByName("newcomer1");
        writeForApplyFormByAllTeam(maybeMember);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>");

        maybeMember.ifPresent(applyMember -> {
            log.info("[1] ApplyForms 조회");
            List<ApplyForm> applyForms = applyFormRepository.findByMember(applyMember);

            applyForms.forEach(applyForm -> {
                Set<Approve> approves = applyForm.getApproves();
                System.err.println(approves);
            });
            /**
             * 승인자를 구하기 위해
             * applyForm 엔티티를 기준으로 탐색을 시작한다.
             * 스트림을 통해 데이터를 가공하는 과정에서 Approve 조회 쿼리가 발생하게 된다.
             *
             * 예를 들자면, 신청서에 승인자가 3명이라면
             * 승인자 데이터를 얻기 위해 3번의 조회 쿼리가 발생하게 된다.
             * @author : moong
             * */
            Set<Approve> approves = applyForms.stream()
                    .map(ApplyForm::getApproves)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            log.info("approves : {}", approves.size());
        });

    }

    @Test
    @DisplayName("신청서 저장")
    void testWriteApplyForm() {
        Optional<Member> maybeMember1 = memberRepository.findByName("newcomer1");
        writeForApplyFormByAllTeam(maybeMember1);
//        Optional<Member> maybeMember2 = memberRepository.findByName("newcomer2");
//        writeForApplyFormByAllTeam(maybeMember2);
    }

    @Transactional
    public void writeForApplyFormByAllTeam(Optional<Member> maybeMember) {
        log.info("[Insert] 신청서 시작");
        maybeMember.ifPresent(applyMember -> {
            List<Team> teams = teamRepository.findAll();
            for (Team applyTeam : teams) {
                Set<Approve> approvers = approveRepository.findByTeam(applyTeam);
                ApplyForm saveApplyForm = ApplyForm.write(applyMember, applyTeam, String.format("%s 부서에 신청합니다.", applyTeam.getName()))
                        .notifyForApprovers(approvers);

                log.info("saveApplyForm.getApproves() : {}", saveApplyForm.getApproves());
                ApplyForm applyForm = applyFormRepository.save(saveApplyForm);
                doEntityManagerFlushAndClear();
                log.info("applyForm.getApproves().size() : {}", applyForm.getApproves().size());

                Assertions.assertThat(approvers).isNotEmpty().isEqualTo(applyForm.getApproves());
            }
        });
        log.info("[Insert] ApplyForm END");
    }

    @Test
    @DisplayName("신청서 승인 상태 변경")
    void testChangeApplyFormStatus() {
        testWriteApplyForm();

        Optional<Member> maybeMember = memberRepository.findByName("kim");
        log.info("approve member -> {}", maybeMember);
        maybeMember.ifPresent(approveMember -> {
            List<ApplyForm> applyForms = applyFormRepository.findByApproveMember(approveMember);
            log.info("retrieve is apply form -> {}", applyForms);
        });
    }
}