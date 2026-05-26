package com.example.martyrs.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        String mysqlUrl = System.getenv("MYSQL_URL");
        if (mysqlUrl == null) {
            throw new IllegalStateException("MYSQL_URL environment variable is not set");
        }

        if (mysqlUrl.startsWith("jdbc:")) {
            return buildDataSource(mysqlUrl, System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASSWORD"));
        }

        try {
            String stripped = mysqlUrl.replace("mysql://", "");
            String[] userInfoAndRest = stripped.split("@", 2);
            String userInfo = userInfoAndRest[0];
            String[] hostPortAndDb = userInfoAndRest[1].split("/", 2);
            String hostPort = hostPortAndDb[0];
            String database = hostPortAndDb.length > 1 ? hostPortAndDb[1].split("\\?")[0] : "railway";
            String[] hostAndPort = hostPort.split(":");
            String host = hostAndPort[0];
            String portStr = hostAndPort.length > 1 ? hostAndPort[1] : "3306";

            String[] userAndPassword = userInfo.split(":", 2);
            String username = userAndPassword[0];
            String password = userAndPassword.length > 1 ? userAndPassword[1] : "";

            String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8",
                host, portStr, database
            );

            return buildDataSource(jdbcUrl, username, password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse MYSQL_URL: " + mysqlUrl, e);
        }
    }

    private DataSource buildDataSource(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username != null ? username : "root");
        config.setPassword(password != null ? password : "");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        return new HikariDataSource(config);
    }
}
