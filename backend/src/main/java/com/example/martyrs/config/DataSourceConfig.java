package com.example.martyrs.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(Environment env) throws URISyntaxException {
        String mysqlUrl = env.getProperty("MYSQL_URL");
        if (mysqlUrl == null || mysqlUrl.startsWith("jdbc:")) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(mysqlUrl);
            config.setUsername(env.getProperty("MYSQL_USER"));
            config.setPassword(env.getProperty("MYSQL_PASSWORD"));
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setMaximumPoolSize(3);
            config.setMinimumIdle(1);
            return new HikariDataSource(config);
        }

        URI uri = new URI(mysqlUrl);
        String host = uri.getHost();
        int port = uri.getPort();
        String database = uri.getPath().replace("/", "");
        String username = env.getProperty("MYSQL_USER", "root");
        String password = env.getProperty("MYSQL_PASSWORD", "");

        if (port <= 0) port = 3306;

        String jdbcUrl = String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8",
            host, port, database
        );

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);

        return new HikariDataSource(config);
    }
}
