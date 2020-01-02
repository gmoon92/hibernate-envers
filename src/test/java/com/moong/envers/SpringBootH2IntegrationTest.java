package com.moong.envers;

import com.moong.envers.global.config.H2ServerConfig;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import com.moong.envers.team.domain.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("메이븐 빌드시 tcp 접속을 다시 시도하면서 에러 발생 ignore 처리")
@Slf4j
@SpringBootTest(classes = { RevListenerDemoApplication.class, H2ServerConfig.class })
class SpringBootH2IntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    void givenRepository_whenSaveAndRetrieveEntity() {
        Team web1 = Team.newTeam("web1");
        Member moon = Member.newMember("moon", "pa$$word", web1);
        Member member = memberRepository.save(moon);
        Member findMember = memberRepository.findById(member.getId()).get();

        assertThat(member).isNotNull().isEqualTo(findMember);
    }
}