package com.moong.envers.approve.repo;

import com.moong.envers.approve.domain.Approve;
import com.moong.envers.approve.types.ApproveStatus;
import com.moong.envers.common.config.BaseJPARepositoryTestCase;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.domain.QMember;
import com.moong.envers.member.repo.MemberRepository;
import com.moong.envers.team.domain.QTeam;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.repo.TeamRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.moong.envers.approve.domain.QApprove.approve;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class ApproveRepositoryTest extends BaseJPARepositoryTestCase {

    private final ApproveRepository approveRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    private Member member;
    private Team team;
    private Team team2;

    @BeforeEach
    void init() {
        memberRepository.deleteAll();
        teamRepository.deleteAll();

        team = teamRepository.save(Team.newTeam("web1"));
        team2 = teamRepository.save(Team.newTeam("web2"));
        member = memberRepository.save(Member.builder()
                .team(team)
                .name("moon")
                .build());
        doEntityManagerFlushAndClear();
    }

    @Test
    @DisplayName("N+1 query 개선")
    void testRetrieveApproveMember() {
        saveSampleApprove();
        log.info(">>>>>>>>>>>>>>>>>");
        log.info(">>>>>>>>>>>>>>>>>");
//        N+1 발생
        List<Approve> approveMembers = approveRepository.findByMember(member);
        log.info("approveMembers {} : {}", approveMembers.size(), approveMembers);
        doEntityManagerFlushAndClear();

        log.info(">>>>>>>>>>>>>>>>>");
        log.info(">>>>>>>>>>>>>>>>>");
//        fetch join으로 개선
        List<Approve> testList = jpaQueryFactory.select(approve)
                .from(approve)
                .leftJoin(approve.member, QMember.member).fetchJoin()
                .leftJoin(approve.team, QTeam.team).fetchJoin()
                .fetch();
        log.info("testList {} : {}", testList.size(), testList);
        Approve approveMember = approveRepository.findByMemberAndTeam(member, member.getTeam());
        log.info("approveMember : {} ", approveMember);
    }

    public void saveSampleApprove() {
        approveRepository.save(Approve.register(member, team));
        approveRepository.save(Approve.register(member, team2));
        doEntityManagerFlushAndClear();
    }

    @Test
    @DisplayName("다대다 저장 연습")
    void testManyToManySave() {
//      [1] 저장
        Approve saveApprove = approveRepository.save(Approve.register(member, team));
        Assertions.assertThat(saveApprove.getStatus())
                .isEqualTo(ApproveStatus.WAIT);
        doEntityManagerFlushAndClear();

//      [2] Approve 상태 변경 Dirty checking 테스트
        Approve acceptApprove = changeApproveStatus(saveApprove.getId(), ApproveStatus.ASSENT);
        Assertions.assertThat(acceptApprove.getStatus())
                .isEqualTo(ApproveStatus.ASSENT);
        doEntityManagerFlushAndClear();

        /**
         * Spring Data JPA의 findOne 메서드의 사용법이 변경되었다.
         * 정확한 Spring 버전은 확인해봐야겠지만, 현재 Spring Boot 2.2.1에서 Example 클래스를 통해 조회할 수 있도록 변경되었다.
         * 추측해보자면 2.0 버전부터 findOne 메서드가 변경된 것으로 파악된다.
         *
         * 이는 QueryDSL 처럼 조회할 쿼리 타입을 지정하여 다이나믹하게 조회할 수 있게 되었다.
         * @author moong
         * */
//      [3] findOne 테스트
        Example<Approve> example = Example.of(Approve.register(member, team).changeApproveStatus(ApproveStatus.ASSENT));
        Optional<Approve> findOneApprove = approveRepository.findOne(example);
        findOneApprove.ifPresent(approve -> {
            log.info("findOneApprove : {}", approve);
            Assertions.assertThat(approve)
                    .isEqualTo(Approve.register(member,team).changeApproveStatus(ApproveStatus.ASSENT));
        });
    }

    @Transactional
    public Approve changeApproveStatus(Approve.Id id, ApproveStatus status) {
        Approve approve = approveRepository.getOne(id)
                .changeApproveStatus(status);
        return approve;
    }
}