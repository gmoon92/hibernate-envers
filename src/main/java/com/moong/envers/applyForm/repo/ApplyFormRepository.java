package com.moong.envers.applyForm.repo;

import com.moong.envers.applyForm.domain.ApplyForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyFormRepository extends JpaRepository<ApplyForm, Long>, ApplyFormRepositoryCustom {

}
