package com.moong.envers.common.config;

import com.moong.envers.common.properties.PersistenceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Optional;
import java.util.Properties;

import static com.moong.envers.common.constants.Profiles.Constants.DEV;
import static com.moong.envers.common.constants.Profiles.Constants.LOCAL;

/**
 * JPA Hibernate Config
 *
 * @author moong
 */
@Slf4j
@Profile(value = { DEV, LOCAL })
@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableConfigurationProperties(PersistenceProperties.class)
public class JPAConfig {

    public final static String PERSISTENCE_UNIT_NAME = "defaultUnit";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PersistenceProperties persistenceProperties;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        em.setDataSource(dataSource);
        em.setPackagesToScan(new String[]{ "com.moong.envers.*.domain" });
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(hibernateProperties());
        return em;
    }

    @Bean
    public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    // todo : AuditorAware 의 구현체 객체 생성
    //  스프링 시큐리티를 사용하여 로그인한 유저를 반환하는 로직 개발하기
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                return Optional.of("gmun.github.io");
            }
        };
    }

    private Properties hibernateProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", persistenceProperties.getHbm2ddl().getAuto());
        hibernateProperties.setProperty("hibernate.dialect", persistenceProperties.getDialect());
        hibernateProperties.setProperty("hibernate.show_sql", persistenceProperties.getShowSql());
        hibernateProperties.setProperty("hibernate.format_sql", persistenceProperties.getFormatSql());
        hibernateProperties.setProperty("hibernate.use_sql_comments", persistenceProperties.getUseSqlComments());
        hibernateProperties.setProperty("org.hibernate.envers.audit_table_suffix", persistenceProperties.getEnvers().getAuditTableSuffix());
        return hibernateProperties;
    }
}
