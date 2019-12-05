package com.moong.envers.approve.repo;

import com.moong.envers.approve.domain.Approve;
import com.moong.envers.approve.domain.QApprove;
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


    /**
     * N+1 발생 해결 방법
     * 1. 엔티티 글로벌 패치 전략 변경
     *  @*One 연관 관계의 글로벌 패치 전략은 기본적으로 EAGER 타입이다.
     *  해당 엔티티를 Lazy로 변경하면 조회할 당시 Proxy 객체로 조회하기 때문에
     *  조회 대상에서 일시적으로 제외됨을 확인할 수 있다.
     *  하지만 근본적인 해결책은 아니다.
     *  결과적으로 해당 객체를 사용할 시점에 Proxy 객체가 초기화 되면서 N+1 이 발생할 수 있다.
     *
     * 2. JPQL의 패러다임 - fetch join
     *  두 번째 해결 방법은 fetch join이다.
     *  조회 시점에 N + 1 이 발생할 수 있는 연관 관계를 엔티티를 조인하여 한번에 가져오는 방법이 있다.
     *  하지만 이 방식도 단점이 존재한다.
     *  2.1. 메모리 문제
     *  2.2. 추가로 관리할 메서드가 증가함에 따른 Repository의 복잡성
     * N+1에 대해 명확한 정리를 할 필요가 있다.
     * 본 코드에선 엔티티에 대한 Lazy 처리와 fetch join 에 대해 두 가지 방식을 적용했다.
     * @author moong
     */
    @Test
    @DisplayName("N+1 query 개선")
    void testNPlushOneProblem() {
        saveSampleApprove();
        log.info(">>>>>>>>>>>>>>>>>");
        log.info(">>>>>>>>>>>>>>>>>");
//        N+1 발생
        List<Approve> approveMembers = approveRepository.findByMember(member);
        log.info("approveMembers {} : {}", approveMembers.size(), approveMembers);
        doEntityManagerFlushAndClear();

        log.info(">>>>>>>>>>>>>>>>>");
        log.info(">>>>>>>>>>>>>>>>>");
//        fetchJoin + FetchType.Lazy 으로 해결
        List<Approve> testList = jpaQueryFactory.select(QApprove.approve)
                .from(QApprove.approve)
                .leftJoin(QApprove.approve.member, QMember.member).fetchJoin()
                .leftJoin(QApprove.approve.team, QTeam.team).fetchJoin()
                .fetch();

        log.info("testList {} : {}", testList.size(), testList);

//        단일 건은 이상 없음
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
        Optional<Approve> maybeApprove = approveRepository.findOne(example);
        maybeApprove.ifPresent(approve -> {
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