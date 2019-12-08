package com.moong.envers.applyForm.repo;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyFormRepository extends JpaRepository<ApplyForm, Long>, ApplyFormRepositoryCustom {

    List<ApplyForm> findByMember(Member applyMember);
}
