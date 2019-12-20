package com.moong.envers.common.config;

import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class BaseDataSettings implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Sample Data settings...");
        Team team = teamRepository.findByName("web1")
                .orElseGet(() -> teamRepository.save(Team.newTeam("web1")));
        memberRepository.findByName("admin")
                .orElseGet(() -> memberRepository.save(Member.builder().name("admin").team(team).build()));
    }
}
