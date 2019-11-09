package com.moong.envers;

import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@RequiredArgsConstructor
public class RevListenerDemoApplication implements CommandLineRunner {

	private final MemberRepository memberRepository;
	private final TeamRepository teamRepository;

	public static void main(String[] args) {
		SpringApplication.run(RevListenerDemoApplication.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		Team team = teamRepository.findByName("web1")
				.orElse(teamRepository.save(Team.builder().name("web1").build()));
		memberRepository.findByName("admin")
				.orElseGet(() -> memberRepository.save(Member.builder().name("admin").team(team).build()));
	}
}
