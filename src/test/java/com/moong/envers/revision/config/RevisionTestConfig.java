package com.moong.envers.revision.config;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.applyForm.repo.ApplyFormRepository;
import com.moong.envers.approve.domain.Approve;
import com.moong.envers.approve.repo.ApproveRepository;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.repo.TeamRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.moong.envers.common.constants.Profiles.Constants.TEST_REV;
import static java.util.Arrays.asList;


/**
 * Revision 데이터는 테스트 코드를 작성할 때 발생하지 않는다.
 * <p>
 * 테스트가 종료되는 시점에 Transaction에 rollback을 한다.
 * 하지만 중요한 점은 아래와 같다.
 * -----------------------------------------------------------------------
 * ------Transaction------------------->|------Transaction commit after--->
 * [1] Entity -> action                 | ---> [2] Audited Entity
 * [insert/update/delete]       |           [insert]
 * -----------------------------------------------------------------------
 * <p>
 * Test code
 * ------Transaction------------------->|------Transaction commit after--->
 * [1] Entity -> action                 | -x-> Audited Entity
 * [insert/update/delete]       |           [insert]
 * [3] rollback <- |
 * -----------------------------------------------------------------------
 * <p>
 * Envers와 관련된 Revision Entity 또는 Audited Entity는 Transaction이 commit된 이후
 * 작업을 수행한다. 따라서 테스트 코드에선 Entity 데이터를 rollback 처리하기 때문에
 * 테스트 코드에서 Audited Entity가 어떻게 동작되는지 살펴보기 위해선 다른 방식을 사용하여 접근해야 한다.
 * 예를 들자면, 1) Transaction을 사전에 commit 하거나 2) Spring의 실행 시점에 Entity 데이터의 값을 넣어 확인해볼 수 있다.
 * <p>
 * 참고
 * https://stackoverflow.com/questions/48359451/testing-hibernate-envers
 * https://stackoverflow.com/questions/11728048/how-to-create-junit-for-the-code-that-uses-envers
 * https://stackoverflow.com/questions/8363815/integration-testing-with-hibernate-envers
 * https://www.baeldung.com/spring-boot-exclude-auto-configuration-test
 *
 * @author moong
 */
@Slf4j
@Profile(value = TEST_REV)
@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RevisionTestConfig {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final ApproveRepository approveRepository;
    private final ApplyFormRepository applyFormRepository;

    @Bean
    @Order(1)
    public CommandLineRunner initRevisionOfADDType() {
        return args -> {
            log.info("Init Revision entity RevisionType.ADD Data...");

            Team web1 = Team.newTeam("web1");
            Team web2 = Team.newTeam("web1");

            teamRepository.saveAll(asList(web1, web2));
            Member kjh = Member.builder().name("kjh").team(web1).build();
            Member lsy = Member.builder().name("lsy").team(web1).build();
            Member jts = Member.builder().name("jts").team(web2).build();

            List<Member> approveWeb1Members = memberRepository.saveAll(asList(kjh, lsy));
            List<Member> approveWeb2Members = memberRepository.saveAll(asList(jts));

            List<Approve> approveWeb1 = Approve.register(approveWeb1Members, web1);
            List<Approve> approveWeb2 = Approve.register(approveWeb2Members, web2);
            List<Approve> approves = approveRepository.saveAll(Stream.of(approveWeb1, approveWeb2)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));

            Member applyMember1 = Member.builder().name("applyMember1").build();
            Member applyMember2 = Member.builder().name("applyMember2").build();

            List<Member> applyMembers = memberRepository.saveAll(asList(applyMember1, applyMember2));

            List<ApplyForm> web1ApplyForm = ApplyForm.write(applyMembers, web1);
            List<ApplyForm> web2ApplyForm = ApplyForm.write(applyMembers, web2);

            applyFormRepository.saveAll(Stream.of(web1ApplyForm, web2ApplyForm)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));

        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner initRevisionOfMODType() {
        return args -> {
            log.info("Init Revision entity RevisionType.MOD Data...");
            Member updateMember = memberRepository.findByName("applyMember1").get()
                    .changePassword("change");
            memberRepository.save(updateMember);
        };
    }

    @Bean
    @Order(3)
    public CommandLineRunner initRevisionOfDELType() {
        return args -> {
            log.info("Init Revision entity RevisionType.DEL Data...");
//            memberRepository.deleteAll();
        };
    }
}
