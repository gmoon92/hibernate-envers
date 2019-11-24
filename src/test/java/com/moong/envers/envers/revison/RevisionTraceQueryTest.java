package com.moong.envers.envers.revison;

import com.moong.envers.common.config.BaseJPARepositoryTestCase;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class RevisionTraceQueryTest extends BaseJPARepositoryTestCase {

    private final MemberRepository memberRepository;
    private Member admin;

    @BeforeEach
    void init() {
        admin = memberRepository.findByName("admin").get();
    }

    @Test
    @DisplayName("다양한 Aud 테이블 조회 방식")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void getEntityAud_세가지_방식() {
        Long rev = 3L;

        AuditReader auditReader = AuditReaderFactory.get(em);

//        [1] RevisionType.DEL 제외 조회
        Member memberAud1 = auditReader.find(Member.class, admin.getId(), rev);

//        query 사용시 해당 rev 없다면, 예외 발생 javax.persistence.NoResultException
//        [2] RevisionType 제어 가능 조회
        Member memberAud2 = (Member)auditReader.createQuery()
                .forRevisionsOfEntity(Member.class, true, false)
                .add(AuditEntity.revisionNumber().eq(rev))
                .add(AuditEntity.id().eq(admin.getId()))
                .getSingleResult();

//        [3] 순수 query 작성 가능
        Member memberAud3 = (Member) auditReader.createQuery()
                .forEntitiesModifiedAtRevision(Member.class, rev)
                .add(AuditEntity.id().eq(admin.getId()))
                .getSingleResult();
    }

    @Test
    public void getPreModifiedRevision() {
    }

    @Test
    public void getModifiedEntities() {
    }

    @AfterEach
    public void doEntityManagerFlushAndClear() {
        log.info("close...");
        em.flush();
        em.clear();
    }
}