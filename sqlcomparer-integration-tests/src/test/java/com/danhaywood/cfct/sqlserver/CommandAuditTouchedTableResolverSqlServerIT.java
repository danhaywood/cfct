package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.harness.DatabaseSide;
import com.danhaywood.cfct.harness.SqlServerTestHarness;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

@Tag("integration")
class CommandAuditTouchedTableResolverSqlServerIT {

    private static final String REGISTER_PRODUCT_INTERACTION_ID = "11111111-1111-1111-1111-111111111111";
    private static final String SUPPLIER_UPDATE_INTERACTION_ID = "22222222-2222-2222-2222-222222222222";
    private static final String PRODUCT_STATUS_INTERACTION_ID = "33333333-3333-3333-3333-333333333333";

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
    void approvesTouchedTablesForSingleInteraction() throws Exception {
        initializeFixture("purchase-order");

        final List<String> interactionIds = List.of(REGISTER_PRODUCT_INTERACTION_ID);
        final SortedSet<String> touchedTables;
        try (Connection connection = harness.openConnection(DatabaseSide.LEFT)) {
            touchedTables = resolver.resolveTouchedQualifiedTableNames(connection, interactionIds);
        }

        Approvals.verify(renderResolution("single interaction", interactionIds, touchedTables));
    }

    @Test
    void approvesTouchedTablesForMultipleInteractionsUnion() throws Exception {
        initializeFixture("purchase-order");
        harness.initializeFromResource(DatabaseSide.LEFT, "/sql/fixtures/purchase-order/left-extra-command-audit-interactions.sql");

        final List<String> interactionIds = List.of(
                REGISTER_PRODUCT_INTERACTION_ID,
                SUPPLIER_UPDATE_INTERACTION_ID,
                PRODUCT_STATUS_INTERACTION_ID);

        final SortedSet<String> touchedTables;
        try (Connection connection = harness.openConnection(DatabaseSide.LEFT)) {
            touchedTables = resolver.resolveTouchedQualifiedTableNames(connection, interactionIds);
        }

        Approvals.verify(renderResolution("multiple interactions", interactionIds, touchedTables));
    }

    private static String renderResolution(
            final String scenario,
            final Collection<String> interactionIds,
            final SortedSet<String> touchedTables) {
        return """
                scenario: %s
                interactionIds:
                %s
                touchedTables:
                %s
                """.formatted(
                scenario,
                interactionIds.stream().map(id -> "- " + id).reduce((a, b) -> a + "\n" + b).orElse("(none)"),
                touchedTables.stream().map(table -> "- " + table).reduce((a, b) -> a + "\n" + b).orElse("(none)")
        );
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
