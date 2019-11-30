package com.moong.envers.common.config;

import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
@TestConstructor(autowireMode = ALL)
public abstract class BaseJPARepositoryTestCase extends BaseTestCase {

    @PersistenceContext(name = JPAConfig.PERSISTENCE_UNIT_NAME)
    public EntityManager em;

    @AfterEach
    public void cleanEntityManager() {
        doEntityManagerFlushAndClear();
    }

    protected void doEntityManagerFlushAndClear() {
        log.trace("Do action Entity manager flush and clear...");
        em.flush();
        em.clear();
    }
}
