package com.danhaywood.sqlcomparer.sqlserver;

import com.danhaywood.sqlcomparer.harness.DatabaseSide;
import com.danhaywood.sqlcomparer.harness.SqlServerTestHarness;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class CommandAuditTouchedTableResolverSqlServerIT {

    private static SqlServerTestHarness harness;

    private final CommandAuditTouchedTableResolverSqlServer resolver = new CommandAuditTouchedTableResolverSqlServer();

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
    void resolvesTouchedTablesForSingleInteraction() throws Exception {
        initializeFixture("purchase-order");

        try (Connection connection = harness.openConnection(DatabaseSide.LEFT)) {
            assertThat(resolver.resolveTouchedQualifiedTableNames(connection,
                    List.of("11111111-1111-1111-1111-111111111111")))
                    .containsExactly("dbo.Product", "dbo.ProductInventory");
        }
    }

    @Test
    void resolvesDistinctDeterministicUnionAcrossMultipleInteractions() throws Exception {
        initializeFixture("purchase-order");
        seedAdditionalInteractions(DatabaseSide.LEFT);

        try (Connection connection = harness.openConnection(DatabaseSide.LEFT)) {
            assertThat(resolver.resolveTouchedQualifiedTableNames(connection,
                    List.of(
                            "11111111-1111-1111-1111-111111111111",
                            "22222222-2222-2222-2222-222222222222",
                            "33333333-3333-3333-3333-333333333333")))
                    .containsExactly("dbo.Product", "dbo.ProductInventory", "dbo.Supplier");
        }
    }

    private static void seedAdditionalInteractions(final DatabaseSide side) {
        harness.executeScript(side, """
                INSERT INTO causewayExtCommandLog.CommandLogEntry (
                    interactionId,
                    executeIn,
                    logicalMemberIdentifier,
                    [timestamp],
                    target,
                    replayState
                ) VALUES
                    ('22222222-2222-2222-2222-222222222222', 'FOREGROUND', 'supplier.Supplier#updateName', '2026-04-05T10:30:00.000', 'supplier.Supplier:302', 'EXPORTED'),
                    ('33333333-3333-3333-3333-333333333333', 'FOREGROUND', 'product.Product#changeStatus', '2026-04-05T11:00:00.000', 'product.Product:702', 'EXPORTED');

                INSERT INTO causewayExtAuditTrail.AuditTrailEntry (
                    interactionId,
                    sequence,
                    target,
                    propertyId
                ) VALUES
                    ('22222222-2222-2222-2222-222222222222', 1, 'supplier.Supplier:302', 'name'),
                    ('22222222-2222-2222-2222-222222222222', 2, 'malformedTargetWithoutSeparator', 'ignored'),
                    ('22222222-2222-2222-2222-222222222222', 3, 'unknown.Type:1', 'ignored'),
                    ('33333333-3333-3333-3333-333333333333', 1, 'product.Product:702', 'status');
                """);
    }

    private static void initializeFixture(final String fixtureName) {
        initializeFixture(DatabaseSide.LEFT, fixtureName, "/sql/fixtures/%s/left-data.sql".formatted(fixtureName));
        initializeFixture(DatabaseSide.RIGHT, fixtureName, "/sql/fixtures/%s/right-data.sql".formatted(fixtureName));
    }

    private static void initializeFixture(final DatabaseSide side, final String fixtureName, final String dataResourcePath) {
        harness.initializeFromResource(side, "/sql/fixtures/%s/schema.sql".formatted(fixtureName));
        harness.initializeFromResource(side, dataResourcePath);
    }
}
