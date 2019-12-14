package com.moong.envers.envers.revison;

import com.moong.envers.common.config.BaseJPARepositoryTestCase;
import com.moong.envers.common.config.SampleDataSettings;
import com.moong.envers.envers.config.RevisionHistoryModifiedEventListener;
import com.moong.envers.envers.types.RevisionTarget;
import com.moong.envers.member.domain.Member;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.util.Optional;

import static com.moong.envers.envers.domain.QRevisionHistoryModified.revisionHistoryModified;
import static com.moong.envers.member.domain.QMember.member;

@Import( { SampleDataSettings.class})
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class AuditedEntityRepositoryImplTest extends BaseJPARepositoryTestCase {

    private SessionFactory sessionFactory;
    private final EntityManagerFactory factory;
    private Long entityId;
    private Long rev;

    @PostConstruct
    public void registerListeners() {
        this.sessionFactory = Optional.ofNullable(factory.unwrap(SessionFactory.class))
                .orElseThrow(() -> new NullPointerException("factory is not a hibernate factory"));
        EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
        EventListenerGroup<PostInsertEventListener> eventListenerGroup = registry.getEventListenerGroup(EventType.POST_COMMIT_INSERT);
        eventListenerGroup.clear();
        eventListenerGroup.appendListener(new RevisionHistoryModifiedEventListener(em));
    }

    @BeforeEach
    void init() {
        entityId = jpaQueryFactory.select(member.id.max())
                .from(member)
                .fetchOne();
        rev = jpaQueryFactory.select(revisionHistoryModified.revision.id.max())
                .from(revisionHistoryModified)
                .where(revisionHistoryModified.entityId.eq(String.valueOf(entityId))
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
                .getSingleResult();
        log.info("getEntityAud_세가지_방식 [3] 순수 query 작성 가능 : {}", memberAud3);
    }

    //      switch (target) {
//        case AGENT_GROUP_USER:
//          AgentGroupUser.Id agentGroupUserId = AgentGroupUser.Id.from(entityId);
//          query.add(AuditEntity.property("id.agentGroupId").eq(agentGroupUserId.getAgentGroupId()));
//          query.add(AuditEntity.property("id.userId").eq(agentGroupUserId.getUserId()) );
//          break;
//        case AGENT_USER:
//          AgentUser.Id agentUserId = AgentUser.Id.from(entityId);
//          query.add(AuditEntity.property("id.agentId").eq(agentUserId.getAgentId()));
//          query.add(AuditEntity.property("id.userId").eq(agentUserId.getUserId()));
//          break;
//        default:
//          query.add(AuditEntity.id().eq(entityId));
//          break;
//      }
}