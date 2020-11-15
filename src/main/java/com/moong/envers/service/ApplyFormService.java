package com.moong.envers.service;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyFormService {

    private final MemberRepository memberRepository;

    private final TeamRepository teamRepository;

    private final ApplyFormRepository applyFormRepository;

    private final ApproveRepository approveRepository;

    public Set<Approve> getApprovers(ApplyForm applyForm) {
        return applyFormRepository.findByMember(applyForm.getMember()).stream()
                .map(ApplyForm::getApproves)
                .flatMap(Collection::stream)
                .peek(approve -> log.debug("applyForm approver name : {}", approve.getMember().getName()))
                .collect(Collectors.toSet());
    }

    public void removeApplyForm(Long applyFormId) {
        applyFormRepository.findById(applyFormId)
                .ifPresent(applyForm -> {
                    log.debug("Remove Apply Form id : {}, applyTeam : {}, applyMember : {}, content : {}", applyForm.getId(), applyForm.getMember().getName(), applyForm.getTeam().getName(), applyForm.getContent());
                    applyFormRepository.delete(applyForm);
                });
    }

    /**
     * todo : 1. Service parameter null check aop 개발
     *        2. 신청자를 SecurityContext 에 등록된 로그인 유저로 대체
     * @author moong
     * */
    public void writeApplyForm(ApplyFormVO applyFormVO) {
        teamRepository.findByName(applyFormVO.getApplyTeamName())
                .ifPresent(applyTeam -> writeApplyForm(applyFormVO, applyTeam));
    }

    public void writeApplyFormByAllTeam(ApplyFormVO applyFormVO) {
        teamRepository.findAll().forEach(applyTeam -> writeApplyForm(applyFormVO, applyTeam));
    }

    private void writeApplyForm(ApplyFormVO applyFormVO, Team applyTeam) {
        String applyUserName = applyFormVO.getApplyUserName();
        Member applyMember = memberRepository.findByName(applyUserName)
                .orElseThrow(() -> new RuntimeException(String.format("Not found apply member about applyForm... username : %s", applyUserName)));

        Set<Approve> approves = approveRepository.findByTeam(applyTeam);
        ApplyForm writeApplyForm = ApplyForm.write(applyMember, applyTeam, applyFormVO.getContent())
                .notifyForApprover(approves);
        applyFormRepository.save(writeApplyForm);
    }


}
