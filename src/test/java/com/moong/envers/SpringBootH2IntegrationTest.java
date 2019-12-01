package com.moong.envers;

import com.moong.envers.common.config.H2ServerConfig;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Disabled("메이븐 빌드시 tcp 접속을 다시 시도하면서 에러 발생 ignore 처리")
@Slf4j
@SpringBootTest(classes = { RevListenerDemoApplication.class, H2ServerConfig.class })
class SpringBootH2IntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    void givenRepository_whenSaveAndRetrieveEntity() {
        Member member = memberRepository.save(Member.builder().name("moon").build());
        Member findMember = memberRepository.findById(member.getId()).get();

        Assertions.assertThat(member).isNotNull().isEqualTo(findMember);
    }
}