package com.moong.envers.approve.repo;

import com.moong.envers.approve.domain.Approve;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApproveRepository extends JpaRepository<Approve, Approve.Id> {
}
