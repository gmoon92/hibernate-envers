package com.moong.envers.applyForm.repo;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.member.domain.Member;

import java.util.List;

/**
 * public이 아닌 default 접근 제한자를 지정하여
 * 해당 패키지 내에서만 접근이 가능하도록 설정했다.
 * 이러한 이유는 해당 인터페이스는 QueryDSL를 활용하여 커스텀한 interface이기 때문이다.
 * 결과적으로 통합된 ApplyFormRepository 인터페이스만을 이용하여 접근을 할 수 있도록 하였다.
 *
 * @author gmoon
 */
interface ApplyFormRepositoryCustom {
    List<ApplyForm> findApplyFormsToApprove(Member approveMember);
}
