package com.danhaywood.cfct.webapp.e2e;

import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

final class PlaywrightSqlServerContainerSupport {

    private static final MSSQLServerContainer<?> SQL_SERVER = new MSSQLServerContainer<>(
            DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest"))
            .acceptLicense()
            .withEnv("MSSQL_PID", "Developer")
            .withPassword("Str0ng_password!123");

    static {
        SQL_SERVER.start();
    }

    private PlaywrightSqlServerContainerSupport() {
    }

    static MSSQLServerContainer<?> sqlServer() {
        return SQL_SERVER;
    }

    static String server() {
        return "%s:%d".formatted(SQL_SERVER.getHost(), SQL_SERVER.getMappedPort(MSSQLServerContainer.MS_SQL_SERVER_PORT));
    }

    static String jdbcUrl() {
        return "jdbc:sqlserver://%s;encrypt=false;trustServerCertificate=true".formatted(server());
    }

    static String username() {
        return SQL_SERVER.getUsername();
    }

    static String password() {
        return SQL_SERVER.getPassword();
    }
}
