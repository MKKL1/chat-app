package com.szampchat.server.tools;

import org.testcontainers.containers.PostgreSQLContainer;

import java.util.function.Supplier;

public class RdbcUrlUtil {

    public static Supplier<Object> getR2dbcUrl(PostgreSQLContainer<?> postgreSQLContainer) {
        return () -> postgreSQLContainer.getJdbcUrl().replaceFirst("jdbc", "r2dbc");
    }
}
