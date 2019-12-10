package com.moong.envers.applyForm.service;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.applyForm.repo.ApplyFormRepository;
import com.moong.envers.applyForm.vo.ApplyFormVO;
import com.moong.envers.approve.domain.Approve;
import com.moong.envers.approve.repo.ApproveRepository;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplyFormService {

    private final MemberRepository memberRepository;

    private final TeamRepository teamRepository;

    private final ApplyFormRepository applyFormRepository;

    private final ApproveRepository approveRepository;

    public Set<Approve> findApprovers(ApplyForm applyForm) {
        return applyFormRepository.findByMember(applyForm.getMember()).stream()
                .map(ApplyForm::getApproves)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public void removeApplyForm(ApplyForm applyForm) {
        applyFormRepository.remove(applyForm);
    }

    /**
     * todo : 1. Service parameter null check aop 개발
     *        2. 신청자를 SecurityContext 에 등록된 로그인 유저로 대체
     * @author moong
     * */
    public void writeApplyForm(ApplyFormVO applyFormVO) {
        Optional<Team> team = teamRepository.findByName(applyFormVO.getApplyTeamName());
        team.ifPresent(applyTeam -> {
            Member applyMember = memberRepository.findByName(applyFormVO.getApplyUserName())
                    .orElseGet(() -> {throw new RuntimeException(String.format("[Error] Find not Member by username : %s", applyFormVO.getApplyUserName()));});
            applyFormRepository.save(ApplyForm.write(applyMember, applyTeam, applyFormVO.getContent()));
        });
    }

    public void writeApplyFormByAllTeam(ApplyFormVO applyFormVO) {
        teamRepository.findAll().forEach(applyTeam -> {
            Member applyMember = memberRepository.findByName(applyFormVO.getApplyUserName())
                    .orElseGet(() -> {throw new RuntimeException(String.format("[Error] Find not Member by username : %s", applyFormVO.getApplyUserName()));});

            Set<Approve> approves = approveRepository.findByTeam(applyTeam);
            applyFormRepository.save(ApplyForm.write(applyMember, applyTeam, applyFormVO.getContent()))
                    .notifyForApprover(approves);
        });
    }


}
