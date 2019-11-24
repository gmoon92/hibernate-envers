package com.moong.envers;

import com.moong.envers.common.config.BaseJPARepositoryTestCase;
import com.moong.envers.common.config.H2ServerConfig;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest(classes = {RevListenerDemoApplication.class, H2ServerConfig.class, BaseJPARepositoryTestCase.class})
class SpringBootH2IntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    void givenRepository_whenSaveAndRetrieveEntity(){
        Member member = memberRepository.save(Member.builder().name("moong").build());
        Member findMember = memberRepository.findById(member.getId()).get();

        Assertions.assertThat(member).isNotNull().isEqualTo(findMember);
    }
}