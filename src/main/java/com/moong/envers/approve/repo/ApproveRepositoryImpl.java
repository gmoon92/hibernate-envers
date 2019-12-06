package com.moong.envers.approve.repo;

import com.moong.envers.approve.domain.Approve;
import com.moong.envers.member.domain.Member;
import com.moong.envers.team.domain.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import static com.moong.envers.approve.domain.QApprove.approve;
import static com.moong.envers.member.domain.QMember.member;
import static com.moong.envers.team.domain.QTeam.team;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ApproveRepositoryImpl implements ApproveRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * # fetchJoin 가볍게 살펴보기.
     * select a.*
     * from approve a
     *  left outer join member b on a.member_id = b.id
     *  left outer join team c on a.team_id = c.id
     * <p>
     * 기본적인 조회할 때 발생하는 프로젝션의 query type 범위는 from 절의 쿼리 타입이다.
     *
     * # fetchJoin 적용한 쿼리
     * select a.* , b.* , c.*
     * from approve a
     *  left outer join member b on a.member_id = b.id
     *  left outer join team c on a.team_id = c.id
     *
     * @author moong
     */
    @Override
    public Set<Approve> findByMember(Member approveMember) {
        return jpaQueryFactory.select(approve)
                .from(approve)
                .innerJoin(approve.member, member).fetchJoin()
                .leftJoin(approve.team, team).fetchJoin()
                .fetch().stream().collect(Collectors.toSet());
    }

    @Override
    public Set<Approve> findByTeam(Team approveTeam) {
        return jpaQueryFactory.select(approve)
                .from(approve)
                .leftJoin(approve.member, member).fetchJoin()
                .innerJoin(approve.team, team).fetchJoin()
                .where(approve.team.eq(approveTeam))
                .fetch().stream().collect(Collectors.toSet());
    }
}
