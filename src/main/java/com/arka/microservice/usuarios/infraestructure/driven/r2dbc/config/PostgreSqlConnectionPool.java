package com.arka.microservice.usuarios.infraestructure.driven.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.client.SSLMode;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PostgresqlConnectionProperties.class)
public class PostgreSqlConnectionPool {

    public static final int INITIAL_SIZE = 5;
    public static final int MAX_SIZE = 10;
    public static final int MAX_IDLE_TIME = 60;
    public static final int CONNECTION_TIMEOUT = 30;

    @Bean
    public ConnectionPool getConnectionConfig(PostgresqlConnectionProperties properties) {

        PostgresqlConnectionConfiguration dbConfiguration = PostgresqlConnectionConfiguration.builder()
                .host(properties.host())
                .port(properties.port())
                .database(properties.database())
                .schema(properties.schema())
                .username(properties.username())
                .password(properties.password())
                .connectTimeout(java.time.Duration.ofSeconds(CONNECTION_TIMEOUT))
                .sslMode(SSLMode.ALLOW)
                .build();

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
                .connectionFactory(new PostgresqlConnectionFactory(dbConfiguration))
                .name("api-postgres-connection-pool")
                .initialSize(INITIAL_SIZE)
                .maxSize(MAX_SIZE)
                .maxIdleTime(java.time.Duration.ofMinutes(MAX_IDLE_TIME))
                .maxCreateConnectionTime(java.time.Duration.ofSeconds(CONNECTION_TIMEOUT))
                .validationQuery("SELECT 1")
                .build();

        return new ConnectionPool(poolConfiguration);
    }
}