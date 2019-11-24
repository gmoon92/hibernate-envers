package com.moong.envers.common.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@Slf4j
@DataJpaTest
@TestConstructor(autowireMode = ALL)
public abstract class BaseJPARepositoryTestCase {

    @PersistenceContext(name = JPAConfig.PERSISTENCE_UNIT_NAME)
    public EntityManager em;

    @AfterEach
    public void cleanEntityManager() {
        doEntityManagerFlushAndClear();
    }

    protected void doEntityManagerFlushAndClear() {
        em.flush();
        em.clear();
    }
}
