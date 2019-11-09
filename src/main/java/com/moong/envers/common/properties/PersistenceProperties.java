package com.moong.envers.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:persistence-h2.properties")
@ConfigurationProperties("hibernate")
@Getter @Setter
public class PersistenceProperties {

    private String dialect;

    private String showSql;

    private String formatSql;

    private String useSqlComments;

    private Hbm2ddl hbm2ddl;

    private Envers envers;

    @Getter @Setter
    public static class Hbm2ddl {
        private String auto;
    }

    @Getter @Setter
    public static class Envers {
        private String auditTableSuffix;
    }
}
