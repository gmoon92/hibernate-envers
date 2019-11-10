package com.moong.envers.common.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Profile("local")
@Configuration
public class H2ServerConfig {

    /**
     * @apiNote H2 TCP Server Config
     * @author moong
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        log.info("Initializing H2 TCP Server");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    /**
     * @apiNote h2 Datasource Config
     * Spring Boot에선 tomcat-jdbc를 기본 Datasource 로 제공
     * 하지만, 2.0 부턴 HikariCP가 Default
     * 현재 프로젝트는 2.2.1 이기 때문에 HikariCP를 사용.
     * spring.datasource로 값을 설정하기 보다는
     * spring.datasource.hikari로 수동/자동 구분없이 설정해야됌
     * @author moong
     */
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() throws SQLException {
        log.info("Initializing Hikari DataSource");
        h2TcpServer();
        return new HikariDataSource();
    }
}
