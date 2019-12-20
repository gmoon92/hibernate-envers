package com.moong.envers.revision.config;

import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import com.moong.envers.team.repo.TeamRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import static com.moong.envers.common.constants.Profiles.Constants.TEST;
import static com.moong.envers.common.constants.Profiles.Constants.TEST_REV;
import static java.util.Arrays.asList;


/**
 * Revision 데이터는 테스트 코드를 작성할 때 발생하지 않는다.
 *
 * 테스트가 종료되는 시점에 Transaction에 rollback을 한다.
 * 하지만 중요한 점은 아래와 같다.
 * -----------------------------------------------------------------------
 * ------Transaction------------------->|------Transaction commit after--->
 * [1] Entity -> action                 | ---> [2] Audited Entity
 *         [insert/update/delete]       |           [insert]
 * -----------------------------------------------------------------------
 *
 * Test code
 * ------Transaction------------------->|------Transaction commit after--->
 * [1] Entity -> action                 | -x-> Audited Entity
 *         [insert/update/delete]       |           [insert]
 *                      [3] rollback <- |
 * -----------------------------------------------------------------------
 *
 * Envers와 관련된 Revision Entity 또는 Audited Entity는 Transaction이 commit된 이후
 * 작업을 수행한다. 따라서 테스트 코드에선 Entity 데이터를 rollback 처리하기 때문에
 * 테스트 코드에서 Audited Entity가 어떻게 동작되는지 살펴보기 위해선 다른 방식을 사용하여 접근해야 한다.
 * 예를 들자면, 1) Transaction을 사전에 commit 하거나 2) Spring의 실행 시점에 Entity 데이터의 값을 넣어 확인해볼 수 있다.
 *
 * 참고
 * https://stackoverflow.com/questions/48359451/testing-hibernate-envers
 * https://stackoverflow.com/questions/11728048/how-to-create-junit-for-the-code-that-uses-envers
 * https://stackoverflow.com/questions/8363815/integration-testing-with-hibernate-envers
 * https://www.baeldung.com/spring-boot-exclude-auto-configuration-test
 * @author gmoon
 * */
@Slf4j
@Profile(value = { TEST, TEST_REV })
@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RevisionTestDataConfig {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @Bean
    @Order(1)
    public CommandLineRunner initRevisionOfADDType() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                log.info("Init Revision entity RevisionType.ADD Data...");
                memberRepository.saveAll(asList(
                         Member.builder().name("member1").build()
                        ,Member.builder().name("member2").build()
                ));
            }
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner initRevisionOfMODType() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                log.info("Init Revision entity RevisionType.MOD Data...");
                Member updateMember = memberRepository.findByName("member1").get()
                        .changePassword("change");
                memberRepository.save(updateMember);
            }
        };
    }

    @Bean
    @Order(3)
    public CommandLineRunner initRevisionOfDELType() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                log.info("Init Revision entity RevisionType.DEL Data...");
                memberRepository.deleteAll();
            }
        };
    }
}
