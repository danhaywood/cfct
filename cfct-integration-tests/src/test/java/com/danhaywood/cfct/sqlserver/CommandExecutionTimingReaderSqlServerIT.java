package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.harness.DatabaseSide;
import com.danhaywood.cfct.harness.SqlServerTestHarness;
import com.danhaywood.cfct.model.CommandExecutionTimingObservation;
import com.danhaywood.cfct.model.ExecutionTimingScope;
import com.danhaywood.cfct.model.ExecutionTimingSide;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class CommandExecutionTimingReaderSqlServerIT {

    private static final String FOREGROUND_ID = "11111111-1111-1111-1111-111111111111";
    private static final String LEFT_COMPLETED_ID = "44444444-4444-4444-4444-444444444444";
    private static final String LEFT_FAILED_ID = "55555555-5555-5555-5555-555555555555";
    private static final String RIGHT_COMPLETED_ID = "77777777-7777-7777-7777-777777777777";

    private static SqlServerTestHarness harness;

    private final CommandExecutionTimingReaderSqlServer reader = new CommandExecutionTimingReaderSqlServer();

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

    @Test
    void readsForegroundTimingIndependentlyFromBothSides() throws Exception {
        initializeFixture();

        final List<CommandExecutionTimingObservation> appA;
        final List<CommandExecutionTimingObservation> appB;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT)) {
            appA = reader.read(left, List.of(FOREGROUND_ID), ExecutionTimingSide.APP_A, ExecutionTimingScope.FOREGROUND);
            appB = reader.read(right, List.of(FOREGROUND_ID), ExecutionTimingSide.APP_B, ExecutionTimingScope.FOREGROUND);
        }

        assertThat(appA).containsExactly(observation(
                ExecutionTimingSide.APP_A,
                ExecutionTimingScope.FOREGROUND,
                FOREGROUND_ID,
                null,
                "supplier.Supplier#registerProduct",
                "OK",
                "2026-04-05T10:00",
                "2026-04-05T10:00:00.590",
                590L));
        assertThat(appB).containsExactly(observation(
                ExecutionTimingSide.APP_B,
                ExecutionTimingScope.FOREGROUND,
                FOREGROUND_ID,
                null,
                "supplier.Supplier#registerProduct",
                "OK",
                "2026-04-05T10:00",
                "2026-04-05T10:00:00.560",
                560L));
    }

    @Test
    void readsCompletedAndFailedBackgroundTimingInDeterministicOrder() throws Exception {
        initializeFixture();

        final List<CommandExecutionTimingObservation> appA;
        final List<CommandExecutionTimingObservation> appB;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT)) {
            appA = reader.read(
                    left,
                    List.of(LEFT_FAILED_ID, LEFT_COMPLETED_ID),
                    ExecutionTimingSide.APP_A,
                    ExecutionTimingScope.BACKGROUND);
            appB = reader.read(
                    right,
                    List.of(RIGHT_COMPLETED_ID),
                    ExecutionTimingSide.APP_B,
                    ExecutionTimingScope.BACKGROUND);
        }

        assertThat(appA)
                .extracting(
                        CommandExecutionTimingObservation::interactionId,
                        CommandExecutionTimingObservation::logicalMemberIdentifier,
                        CommandExecutionTimingObservation::replayState,
                        CommandExecutionTimingObservation::durationMillis)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(
                                LEFT_COMPLETED_ID,
                                "product.Product#repriceFromSupplierUpdate",
                                "UNDEFINED",
                                200L),
                        org.assertj.core.groups.Tuple.tuple(
                                LEFT_FAILED_ID,
                                "product.ProductInventory#syncFromSupplierUpdate",
                                "FAILED",
                                300L));
        assertThat(appA).allSatisfy(observation -> {
            assertThat(observation.scope()).isEqualTo(ExecutionTimingScope.BACKGROUND);
            assertThat(observation.parentInteractionId()).isEqualTo(FOREGROUND_ID);
        });
        assertThat(appB).singleElement().satisfies(observation -> {
            assertThat(observation.interactionId()).isEqualTo(RIGHT_COMPLETED_ID);
            assertThat(observation.parentInteractionId()).isEqualTo(FOREGROUND_ID);
            assertThat(observation.durationMillis()).isEqualTo(400L);
        });
    }

    private static void initializeFixture() throws Exception {
        harness.initializeFromResource(DatabaseSide.LEFT, "/sql/fixtures/purchase-order/schema.sql");
        harness.initializeFromResource(DatabaseSide.LEFT, "/sql/fixtures/purchase-order/left-data.sql");
        harness.initializeFromResource(DatabaseSide.RIGHT, "/sql/fixtures/purchase-order/schema.sql");
        harness.initializeFromResource(DatabaseSide.RIGHT, "/sql/fixtures/purchase-order/right-data.sql");
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Statement statement = left.createStatement()) {
            statement.executeUpdate("""
                    UPDATE causewayExtCommandLog.CommandLogEntry
                    SET startedAt = '2026-04-05T10:00:00.000',
                        completedAt = '2026-04-05T10:00:00.590'
                    WHERE interactionId = '11111111-1111-1111-1111-111111111111';
                    UPDATE causewayExtCommandLog.CommandLogEntry
                    SET parentInteractionId = '11111111-1111-1111-1111-111111111111',
                        startedAt = '2026-04-05T10:01:00.000',
                        completedAt = '2026-04-05T10:01:00.200'
                    WHERE interactionId = '44444444-4444-4444-4444-444444444444';
                    UPDATE causewayExtCommandLog.CommandLogEntry
                    SET parentInteractionId = '11111111-1111-1111-1111-111111111111',
                        replayState = 'FAILED',
                        startedAt = '2026-04-05T10:03:00.000',
                        completedAt = '2026-04-05T10:03:00.300'
                    WHERE interactionId = '55555555-5555-5555-5555-555555555555';
                    """);
        }
        try (Connection right = harness.openConnection(DatabaseSide.RIGHT);
             Statement statement = right.createStatement()) {
            statement.executeUpdate("""
                    UPDATE causewayExtCommandLog.CommandLogEntry
                    SET replayState = 'OK',
                        startedAt = '2026-04-05T10:00:00.000',
                        completedAt = '2026-04-05T10:00:00.560'
                    WHERE interactionId = '11111111-1111-1111-1111-111111111111';
                    UPDATE causewayExtCommandLog.CommandLogEntry
                    SET parentInteractionId = '11111111-1111-1111-1111-111111111111',
                        startedAt = '2026-04-05T10:40:00.000',
                        completedAt = '2026-04-05T10:40:00.400'
                    WHERE interactionId = '77777777-7777-7777-7777-777777777777';
                    """);
        }
    }

    private static CommandExecutionTimingObservation observation(
            final ExecutionTimingSide side,
            final ExecutionTimingScope scope,
            final String interactionId,
            final String parentInteractionId,
            final String memberIdentifier,
            final String replayState,
            final String startedAt,
            final String completedAt,
            final Long durationMillis) {
        return new CommandExecutionTimingObservation(
                side,
                scope,
                interactionId,
                parentInteractionId,
                memberIdentifier,
                replayState,
                startedAt,
                completedAt,
                durationMillis);
    }
}
