package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.harness.DatabaseSide;
import com.danhaywood.cfct.harness.SqlServerTestHarness;
import com.danhaywood.cfct.model.AuditTrailEntryDescriptor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class AuditTrailEntryReaderSqlServerIT {

    private static final String FOREGROUND_ID = "11111111-1111-1111-1111-111111111111";
    private static final String LEFT_BACKGROUND_ID = "66666666-6666-6666-6666-666666666666";
    private static final String RIGHT_BACKGROUND_ID = "77777777-7777-7777-7777-777777777777";

    private static SqlServerTestHarness harness;

    private final AuditTrailEntryReaderSqlServer reader = new AuditTrailEntryReaderSqlServer();

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
    void readsForegroundEntriesIncludingDuplicateSemanticKeys() throws Exception {
        initializeFixture();

        final List<AuditTrailEntryDescriptor> entries;
        try (Connection connection = harness.openConnection(DatabaseSide.LEFT)) {
            entries = reader.read(connection, List.of(FOREGROUND_ID));
        }

        assertThat(entries).containsExactly(
                new AuditTrailEntryDescriptor(FOREGROUND_ID, 1, "supplier.Supplier:301", "status"),
                new AuditTrailEntryDescriptor(FOREGROUND_ID, 2, "product.Product:701", "unit_price"),
                new AuditTrailEntryDescriptor(FOREGROUND_ID, 3, "product.Product:701", "unit_price"));
    }

    @Test
    void readsEachSidesIndependentlyGeneratedBackgroundInteraction() throws Exception {
        initializeFixture();

        final List<AuditTrailEntryDescriptor> appA;
        final List<AuditTrailEntryDescriptor> appB;
        try (Connection left = harness.openConnection(DatabaseSide.LEFT);
             Connection right = harness.openConnection(DatabaseSide.RIGHT)) {
            appA = reader.read(left, List.of(LEFT_BACKGROUND_ID));
            appB = reader.read(right, List.of(RIGHT_BACKGROUND_ID));
        }

        assertThat(appA).extracting(AuditTrailEntryDescriptor::target, AuditTrailEntryDescriptor::memberIdentifier)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("purchaseorder.PurchaseOrder:101", "status"),
                        org.assertj.core.groups.Tuple.tuple("purchaseorder.PurchaseOrderLine:901", "quantity"));
        assertThat(appB).containsExactly(
                new AuditTrailEntryDescriptor(RIGHT_BACKGROUND_ID, 1, "product.Product:702", "status"));
    }

    private static void initializeFixture() {
        harness.initializeFromResource(DatabaseSide.LEFT, "/sql/fixtures/purchase-order/schema.sql");
        harness.initializeFromResource(DatabaseSide.LEFT, "/sql/fixtures/purchase-order/left-data.sql");
        harness.initializeFromResource(DatabaseSide.RIGHT, "/sql/fixtures/purchase-order/schema.sql");
        harness.initializeFromResource(DatabaseSide.RIGHT, "/sql/fixtures/purchase-order/right-data.sql");
    }
}
