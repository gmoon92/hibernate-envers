package com.moong.envers.global.listener;

import com.moong.envers.revision.domain.RevisionHistory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;

import java.io.Serializable;


/**
 * @author moong
 * https://docs.jboss.org/hibernate/core/4.0/devguide/en-US/html/ch15.html
 * https://github.com/hibernate/hibernate-orm/blob/master/documentation/src/test/java/org/hibernate/userguide/envers/EntityTypeChangeAuditTrackingRevisionListenerTest.java
 */
@Slf4j
public class CustomRevisionEntityListener implements EntityTrackingRevisionListener {

    @Override
    public void entityChanged(Class entityClass, String entityName, Serializable entityId, RevisionType revisionType, Object revisionEntity) {
        log.info("EntityTrackingRevisionListener entityChanged start...");

        try {
            RevisionHistory revisionHistory = RevisionHistory.class.cast(revisionEntity);
            if (RevisionType.MOD.equals(revisionType))
                revisionHistory.traceModifiedEntity(entityId, entityClass);
        } catch (Exception e) {
            log.error("Dose not changed revision object...", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void newRevision(Object revisionEntity) {
        // todo : Spring Security를 사용하여 회원의 저장
    }
}
