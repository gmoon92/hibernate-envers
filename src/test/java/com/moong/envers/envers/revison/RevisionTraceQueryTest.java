package com.moong.envers.envers.revison;

import com.moong.envers.common.config.JPAConfig;
import com.moong.envers.envers.domain.RevisionHistory;
import com.moong.envers.member.domain.Member;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.moong.envers.envers.domain.QRevisionHistory.revisionHistory;
import static com.moong.envers.member.domain.QMember.member;

@Slf4j
@SpringBootTest
class RevisionTraceQueryTest {

    @PersistenceContext(unitName = JPAConfig.PERSISTENCE_UNIT_NAME)
    private EntityManager em;

    private Long rev;
    private Long entityId;

    @BeforeEach
    void init() {
        entityId = new JPAQuery<Member>(em)
                .select(member.id.max())
                .from(member)
                .fetchOne();
        rev = new JPAQuery<RevisionHistory>(em)
                .select(revisionHistory.id.max())
                .from(revisionHistory)
                .fetchOne();
    }

    @Test
    @DisplayName("다양한 Aud 테이블 조회 방식")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void getEntityAud_세가지_방식() {
        AuditReader auditReader = AuditReaderFactory.get(em);

//        [1] RevisionType.DEL 제외 조회
        Member memberAud1 = auditReader.find(Member.class, entityId, rev);
        log.info("getEntityAud_세가지_방식 [1] RevisionType.DEL 제외 조회 : {}", memberAud1);

//        query 사용시 해당 rev 없다면, 예외 발생 javax.persistence.NoResultException
//        [2] RevisionType 제어 가능 조회
        Member memberAud2 = (Member)auditReader.createQuery()
                .forRevisionsOfEntity(Member.class, true, false)
                .add(AuditEntity.revisionNumber().eq(rev))
                .add(AuditEntity.id().eq(entityId))
                .getSingleResult();
        log.info("getEntityAud_세가지_방식 [2] RevisionType 제어 가능 조회 : {}", memberAud2);

//        [3] 순수 query 작성 가능
        Member memberAud3 = (Member) auditReader.createQuery()
                .forEntitiesModifiedAtRevision(Member.class, rev)
                .add(AuditEntity.id().eq(entityId))
                .getSingleResult();
        log.info("getEntityAud_세가지_방식 [3] 순수 query 작성 가능 : {}", memberAud3);
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