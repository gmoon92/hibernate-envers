package com.moong.envers.revision.repo;

import com.moong.envers.global.config.BaseRevisionTestConfig;
import com.moong.envers.global.config.BaseServiceTestCase;
import com.moong.envers.member.domain.Member;
import com.moong.envers.revision.types.RevisionTarget;
import com.moong.envers.revision.utils.RevisionConverter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.moong.envers.global.constants.Profiles.Constants.TEST;
import static com.moong.envers.global.constants.Profiles.Constants.TEST_REV;
import static com.moong.envers.member.domain.QMember.member;
import static com.moong.envers.revision.domain.QRevisionHistoryModified.revisionHistoryModified;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = { TEST, TEST_REV })
@Import( { BaseRevisionTestConfig.class})
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class AuditedEntityRepositoryImplTest extends BaseServiceTestCase {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;
    private Long entityId;
    private Long rev;

    @BeforeEach
    void init() {
        entityId = jpaQueryFactory.select(member.id.max())
                .from(member)
                .fetchOne();
        rev = jpaQueryFactory.select(revisionHistoryModified.revision.id.max())
                .from(revisionHistoryModified)
                .where(revisionHistoryModified.entityId.eq(RevisionConverter.serializedObject(entityId))
                        .and(revisionHistoryModified.revisionTarget.eq(RevisionTarget.MEMBER)))
                .fetchOne();

        log.info("entity id : {}, rev : {}", entityId, rev);
    }

    @Test
    @DisplayName("다양한 Aud 테이블 조회 방식")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void getEntityAud_세가지_방식() {
        AuditReader auditReader = AuditReaderFactory.get(em);

//        [1] RevisionType.DEL 제외 조회
//        rev = max(rev.id) 최신 rev
        Member memberAud1 = auditReader.find(Member.class, entityId, rev);
        log.info("getEntityAud_세가지_방식 [1] RevisionType.DEL 제외 조회 : {}", memberAud1);

//        query 사용시 해당 rev 없다면, 예외 발생 javax.persistence.NoResultException
//        [2] RevisionType 제어 가능 조회
        Member memberAud2 = (Member) auditReader.createQuery()
                .forRevisionsOfEntity(Member.class, true, false)
                .add(AuditEntity.revisionNumber().eq(rev))
                .add(AuditEntity.id().eq(entityId))
                .getSingleResult();
        log.info("getEntityAud_세가지_방식 [2] RevisionType 제어 가능 조회 : {}", memberAud2);

//        [3] 순수 query 작성 가능
        Member memberAud3 = (Member) auditReader.createQuery()
                .forEntitiesModifiedAtRevision(Member.class, rev)
                .add(AuditEntity.id().eq(entityId))
//              .add(AuditEntity.property("id.memberId").eq(approve.getId().getMemberId()) );
                .getSingleResult();
        log.info("getEntityAud_세가지_방식 [3] 순수 query 작성 가능 : {}", memberAud3);

        assertThat(memberAud1)
                .isEqualTo(memberAud2)
                .isEqualTo(memberAud3);
    }
}