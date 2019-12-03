package com.moong.envers.approve.repo;

import com.moong.envers.approve.domain.Approve;
import com.moong.envers.member.domain.Member;

import java.util.List;

public interface ApproveRepositoryCustom {
    List<Approve> findByMember(Member approveMember);
}
