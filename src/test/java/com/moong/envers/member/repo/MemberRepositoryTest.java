package com.moong.envers.member.repo;

import com.moong.envers.global.config.BaseJPARepositoryTestCase;
import com.moong.envers.member.domain.Member;
import com.moong.envers.team.domain.Team;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@RequiredArgsConstructor
class MemberRepositoryTest extends BaseJPARepositoryTestCase {

    final MemberRepository memberRepository;

    @BeforeEach
    void init() {
        Team web1 = Team.newTeam("web1");
        em.persist(web1);
        memberRepository.save(Member.newMember("moon", "pa$$word", web1));
        flushAndClear();
    }

    @Test
    @DisplayName("사용자 이름으로 사용자 찾기")
    void testFindByName() {
        Optional<Member> moon = memberRepository.findByName("moon");
        Assertions.assertThat(moon).isNotEmpty();
    }
}