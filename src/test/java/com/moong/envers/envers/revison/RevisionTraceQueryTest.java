package com.moong.envers.envers.revison;

import com.moong.envers.common.config.H2JPAConfig;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Slf4j
@SpringBootTest
public class RevisionTraceQueryTest {

    @PersistenceContext(name = H2JPAConfig.PERSISTENCE_UNIT_NAME)
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    private Member admin;

    @BeforeEach
    public void init() {
        em.flush();
        admin = memberRepository.findByName("admin").get();
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void getEntityAud_세가지_방식() {
        Long rev = 3L;

        AuditReader auditReader = AuditReaderFactory.get(em);

//        [1] RevisionType.DEL 제외 조회
        Member memberAud1 = auditReader.find(Member.class, admin.getId(), rev);

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
    public void close() {
        log.info("close...");
        em.flush();
        em.close();
    }
}