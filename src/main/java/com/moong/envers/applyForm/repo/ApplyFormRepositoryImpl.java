package com.moong.envers.applyForm.repo;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.member.domain.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.moong.envers.applyForm.domain.QApplyForm.applyForm;
import static com.moong.envers.approve.domain.QApprove.approve;

/**
 * Spring Repository Custom 하기
 * 기존 Spring Data JPA에서 제공하는 메소드와
 * QueryDSL를 이용하여 커스텀 Repository를 통합하여 사용할 수 있도록 개선
 * <p>
 * 참고] https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.custom-implementations
 * https://spring.io/blog/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl
 * 그 외 단일 QueryRepository 구성하기 위해선 다음 클래스를 확장하자. {@link QuerydslRepositorySupport}
 *
 * @author moong
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class ApplyFormRepositoryImpl implements ApplyFormRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public List<ApplyForm> findByApproveMember(Member approveMember) {
        return jpaQueryFactory.select(applyForm)
                .from(applyForm)
                .innerJoin(applyForm.approves, approve)
                .where(approve.member.eq(approveMember))
                .fetch();
    }

}
