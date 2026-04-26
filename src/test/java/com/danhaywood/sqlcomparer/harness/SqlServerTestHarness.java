package com.danhaywood.sqlcomparer.harness;

import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class SqlServerTestHarness implements AutoCloseable {

    private static final DockerImageName SQL_SERVER_IMAGE = DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest");
    private static final Duration STARTUP_TIMEOUT = Duration.ofMinutes(5);

    private final MSSQLServerContainer<?> container;

    public SqlServerTestHarness() {
        this.container = new MSSQLServerContainer<>(SQL_SERVER_IMAGE)
                .acceptLicense()
                .withEnv("MSSQL_PID", "Developer")
                .withPassword("Str0ng_password!123")
                .waitingFor(Wait.forLogMessage(".*SQL Server is now ready for client connections.*\\s*", 1))
                .withStartupTimeout(STARTUP_TIMEOUT);
    }

    public SqlServerTestHarness start() {
        container.start();
        Arrays.stream(DatabaseSide.values()).forEach(this::createDatabaseIfMissing);
        return this;
    }

    public String jdbcUrlFor(final DatabaseSide side) {
        return "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=false;trustServerCertificate=true"
                .formatted(container.getHost(), container.getMappedPort(MSSQLServerContainer.MS_SQL_SERVER_PORT), side.databaseName());
    }

    public String username() {
        return container.getUsername();
    }

    public String password() {
        return container.getPassword();
    }

    public void initializeFromResource(final DatabaseSide side, final String resourcePath) {
        executeScript(side, readResource(resourcePath));
    }

    public void executeScript(final DatabaseSide side, final String script) {
        final String[] statements = script.split("(?im)^GO\\s*$");
        try (Connection connection = connectionFor(side)) {
            for (final String statementText : statements) {
                final String sql = statementText.trim();
                if (sql.isBlank()) {
                    continue;
                }
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to execute SQL for database %s".formatted(side.databaseName()), ex);
        }
    }

    public String queryForString(final DatabaseSide side, final String sql) {
        try (Connection connection = connectionFor(side);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (!resultSet.next()) {
                throw new IllegalStateException("Query returned no rows for database %s".formatted(side.databaseName()));
            }
            return resultSet.getString(1);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to query database %s".formatted(side.databaseName()), ex);
        }
    }

    public int queryForInt(final DatabaseSide side, final String sql) {
        try (Connection connection = connectionFor(side);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (!resultSet.next()) {
                throw new IllegalStateException("Query returned no rows for database %s".formatted(side.databaseName()));
            }
            return resultSet.getInt(1);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to query database %s".formatted(side.databaseName()), ex);
        }
    }

    public String describeDatabases() {
        return Arrays.stream(DatabaseSide.values())
                .map(this::describeDatabase)
                .collect(Collectors.joining(System.lineSeparator() + System.lineSeparator()));
    }

    @Override
    public void close() {
        container.close();
    }

    private String describeDatabase(final DatabaseSide side) {
        final String databaseName = queryForString(side, "SELECT DB_NAME()");
        final int rowCount = queryForInt(side, "SELECT COUNT(*) FROM dbo.sample_items");
        final String payload = queryForString(side, "SELECT TOP 1 payload FROM dbo.sample_items ORDER BY payload");
        return "database=%s%nrowCount=%d%npayload=%s".formatted(databaseName, rowCount, payload);
    }

    private void createDatabaseIfMissing(final DatabaseSide side) {
        final String sql = "IF DB_ID(?) IS NULL EXEC('CREATE DATABASE [%s]')".formatted(side.databaseName());
        try (Connection connection = adminConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, side.databaseName());
            statement.execute();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to create database %s".formatted(side.databaseName()), ex);
        }
    }

    private Connection adminConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:sqlserver://%s:%d;databaseName=master;encrypt=false;trustServerCertificate=true"
                        .formatted(container.getHost(), container.getMappedPort(MSSQLServerContainer.MS_SQL_SERVER_PORT)),
                username(),
                password());
    }

    private Connection connectionFor(final DatabaseSide side) throws SQLException {
        return DriverManager.getConnection(jdbcUrlFor(side), username(), password());
    }

    private String readResource(final String resourcePath) {
        try (var inputStream = SqlServerTestHarness.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: %s".formatted(resourcePath));
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to read resource: %s".formatted(resourcePath), ex);
        }
    }
}
