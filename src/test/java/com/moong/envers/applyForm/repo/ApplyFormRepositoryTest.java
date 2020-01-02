package com.moong.envers.applyForm.repo;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.approve.domain.Approve;
import com.moong.envers.approve.repo.ApproveRepository;
import com.moong.envers.approve.types.ApproveStatus;
import com.moong.envers.global.config.BaseJPARepositoryTestCase;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class ApplyFormRepositoryTest extends BaseJPARepositoryTestCase {

    private final ApplyFormRepository applyFormRepository;
    private final ApproveRepository approveRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    private Team web1;
    private Team web2;

    private Member kim;
    private Member lee;
    private Member moon;
    private Member jts;

    private final String DEFAULT_PASSWORD = "pa$$word";

    @BeforeEach
    void init() {
        web1 = Team.newTeam("web1");
        web2 = Team.newTeam("web2");
        teamRepository.saveAll(Arrays.asList(web1, web2));

        kim = Member.newMember("kim", DEFAULT_PASSWORD, web1);
        lee = Member.newMember("lee", DEFAULT_PASSWORD, web1);
        moon = Member.newMember("moon", DEFAULT_PASSWORD, web1);
        jts = Member.newMember("jts", DEFAULT_PASSWORD, web1);

        Member newcomer1 = Member.newMember("newcomer1", DEFAULT_PASSWORD, web1);
        Member newcomer2 = Member.newMember("newcomer2", DEFAULT_PASSWORD, web1);
        memberRepository.saveAll(Arrays.asList(kim, lee, moon, jts, newcomer1, newcomer2));

        approveRepository.saveAll(Arrays.asList(
                Approve.register(kim, web1)
                , Approve.register(lee, web1)
                , Approve.register(jts, web2)
        ));
        flushAndClear();
    }

    @Test
    @DisplayName("신청서 저장 테스트")
    void testApplyFormSave() {
        ApplyForm applyForm = applyFormRepository.save(ApplyForm.write(moon, web1, "신청 테스트입니다."));
        assertThat(applyForm.getStatus()).isEqualTo(ApproveStatus.WAIT);
    }

    @Test
    @DisplayName("신청자에 대한 승인자 조회")
    void testRetrieveApproverAboutApplyForm() {
        Optional<Member> maybeMember = memberRepository.findByName("newcomer1");
        writeForApplyFormByAllTeam(maybeMember);

        maybeMember.ifPresent(applyMember -> {
            log.info("[1] ApplyForms 조회");
            List<ApplyForm> applyForms = applyFormRepository.findByMember(applyMember);

            applyForms.forEach(applyForm -> {
                Set<Approve> approves = applyForm.getApproves();
                log.info("approves : {}", approves.size());
            });
            /**
             * 승인자를 구하기 위해
             * applyForm 엔티티를 기준으로 탐색을 시작한다.
             * 스트림을 통해 데이터를 가공하는 과정에서 Approve 조회 쿼리가 발생하게 된다.
             *
             * 예를 들자면, 신청서에 승인자가 3명이라면
             * 승인자 데이터를 얻기 위해 3번의 조회 쿼리가 발생하게 된다.
             * @author moong
             * */
            Set<Approve> approves = applyForms.stream()
                    .map(ApplyForm::getApproves)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            log.info("approves : {}", approves.size());
        });
    }

    @Test
    @DisplayName("신청서 삭제, 단, 승인자는 삭제하지 않는다.")
    void testDeleteApplyForm() {
        Optional<Member> maybeMember1 = memberRepository.findByName("newcomer1");
        writeForApplyFormByAllTeam(maybeMember1);

        ApplyForm applyForm = applyFormRepository.findByMember(maybeMember1.get())
                .stream().findFirst().get();

        log.info("Collection 부가적인 쿼리가 발생하지 않도록 queryDsl 커스텀 삭제 쿼리 생성");
        approveRepository.changeApplyFormByApprove(applyForm.getApproves(), null);
        applyFormRepository.delete(applyForm);
        flushAndClear();

        /**
         * parent domain remove width out child remove
         * 부모 테이블을 삭제할 때 자식 테이블도 삭제를 자동적으로 되길 원한다면 두 가지 방법이있다.
         *
         * 1. orphanRemoval = true
         * 2. CascadeType.REMOVE
         *
         * 첫 번째 방법은 orphanRemoval 옵션을 지정하는 방식이다.
         * 이 방식은 객체의 레퍼런스가 끊어지게 되면 delete 쿼리를 발생하게 된다.
         *
         * 반면 CascadeType.REMOVE인 경우, 레퍼런스의 연결이 끊어진다고 해서 자동 삭제되는 방식이 아닌,
         * 명시적으로 레파지토리 또는 엔티티 매니저를 통해 해당 도메인 객체를 삭제할 때 같이 삭제됨을 의미하는
         * 영속성 전이와 관련된 옵션이다.
         *
         * 다음 코드를 보며 정리하자면, 고아 객체 제거 여부에 따라도 달리 해석할 수 있다.
         * 1) Cascade.REMOVE
         * @Entity
         * class Parent{
         *  @OneToMany(cascade=Cascade.REMOVE)
         *  private List<Child> childes;
         * }
         * Parent 엔티티가 삭제될 때 연관된 Child 엔티티도 삭제
         *
         * 2) orphanRemoval
         * @Entity
         * class Parent{
         *  @OneToMany(orphanRemoval = true)
         *  private List<Child> childes;
         * }
         * Collection childes 에서 Child 객체가 제거되는 경우 DB에서도 삭제
         *
         * 참고
         * https://stackoverflow.com/questions/4329577/jpa-2-0-orphanremoval-true-vs-on-delete-cascade
         * https://m.blog.naver.com/PostView.nhn?blogId=heops79&logNo=220734819674&proxyReferer=https%3A%2F%2Fwww.google.com%2F
         *
         * @author moong
         */
        log.info("성능문제로 Approve.ApplyForm Update query 1번만 요청하도록 변경");
        applyFormRepository.delete(applyForm);
        flushAndClear();
    }

    @Test
    @DisplayName("신청서 저장")
    void testWriteApplyForm() {
        Optional<Member> maybeMember1 = memberRepository.findByName("newcomer1");
        writeForApplyFormByAllTeam(maybeMember1);

        Optional<Member> maybeMember2 = memberRepository.findByName("newcomer2");
        writeForApplyFormByAllTeam(maybeMember2);
    }

    public void writeForApplyFormByAllTeam(Optional<Member> maybeMember) {
        log.info("[Insert] ApplyForm START");
        maybeMember.ifPresent(applyMember -> {
            List<Team> teams = teamRepository.findAll();
            for (Team applyTeam : teams) {
                Set<Approve> approvers = approveRepository.findByTeam(applyTeam);
                ApplyForm applyForm = applyFormRepository.save(ApplyForm.write(applyMember, applyTeam))
                        .notifyForApprover(approvers);

                assertThat(approvers)
                        .isNotEmpty()
                        .isEqualTo(applyForm.getApproves());
            }
        });
        log.info("[Insert] ApplyForm END");
        flushAndClear();
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