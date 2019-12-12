package com.moong.envers.common.config;

import com.moong.envers.envers.config.RevisionHistoryModifiedEventListener;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import javax.persistence.EntityManager;

/**
 * @apiNote revision entity listener event 처리
 *  POST_COMMIT_INSERT을 한 이유.
 *  현재 인터셉터한 리비젼이 대상이되는 테이블의 데이터를 조회할 때
 *  대상 테이블과 리비젼은 하나의 세션, 트랜잭션으로 이뤄져있어 DB에 값을 불러오지 않는다.
 *  참고 : https://vladmihalcea.com/hibernate-event-listeners/
 * @author moong
 */
public class JPAEventListenerIntegrator implements Integrator {

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
        final EventListenerRegistry registry = sessionFactoryServiceRegistry.getService(EventListenerRegistry.class);

        EntityManager em = sessionFactoryImplementor.createEntityManager();

        registry.appendListeners(EventType.POST_COMMIT_INSERT, new RevisionHistoryModifiedEventListener(em));
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {

    }
}
