package com.danhaywood.sqlcomparer.harness;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@Testcontainers(disabledWithoutDocker = true)
class SqlServerHarnessIT {

    private static SqlServerTestHarness harness;

    @BeforeAll
    static void startHarness() {
        harness = new SqlServerTestHarness().start();
    }

    @AfterAll
    static void stopHarness() {
        if (harness != null) {
            harness.close();
        }
    }

    @ParameterizedTest
    @EnumSource(DatabaseSide.class)
    void connectsToEachLogicalDatabase(final DatabaseSide side) {
        assertThat(harness.queryForString(side, "SELECT DB_NAME()"))
                .isEqualTo(side.databaseName());
    }

    @Test
    void initializesDatabasesIndependentlyAndPreservesIsolation() {
        initializeDatabases();

        assertThat(harness.queryForString(DatabaseSide.LEFT, "SELECT TOP 1 payload FROM dbo.sample_items ORDER BY payload"))
                .isEqualTo("left payload");
        assertThat(harness.queryForString(DatabaseSide.RIGHT, "SELECT TOP 1 payload FROM dbo.sample_items ORDER BY payload"))
                .isEqualTo("right payload");
        assertThat(harness.queryForInt(DatabaseSide.LEFT, "SELECT COUNT(*) FROM dbo.sample_items"))
                .isEqualTo(1);
        assertThat(harness.queryForInt(DatabaseSide.RIGHT, "SELECT COUNT(*) FROM dbo.sample_items"))
                .isEqualTo(1);
    }

    @Test
    void approvesStableHarnessDescription() {
        initializeDatabases();

        Approvals.verify(harness.describeDatabases());
    }

    private static void initializeDatabases() {
        harness.initializeFromResource(DatabaseSide.LEFT, "/sql/left-init.sql");
        harness.initializeFromResource(DatabaseSide.RIGHT, "/sql/right-init.sql");
    }
}
