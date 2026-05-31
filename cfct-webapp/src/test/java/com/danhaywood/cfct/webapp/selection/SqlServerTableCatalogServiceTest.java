package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.model.TableRef;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SqlServerTableCatalogServiceTest {

    @Test
    void excludesCommandAuditAndLogicalTypeMappingTables() {
        assertThat(SqlServerTableCatalogService.isExcludedTable(new TableRef("causewayExtCommandLog", "CommandLogEntry"))).isTrue();
        assertThat(SqlServerTableCatalogService.isExcludedTable(new TableRef("causewayExtAuditTrail", "AuditTrailEntry"))).isTrue();
        assertThat(SqlServerTableCatalogService.isExcludedTable(new TableRef("util", "LogicalTypeTableMapping"))).isTrue();
        assertThat(SqlServerTableCatalogService.isExcludedTable(new TableRef("dbo", "Supplier"))).isFalse();
    }

    @Test
    void mapsEligibleTableWhenExactlyOneBkIndexExists() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "Supplier"),
                1,
                null);

        assertThat(entry.eligible()).isTrue();
        assertThat(entry.eligibilityReason()).isNull();
    }

    @Test
    void mapsIneligibleTableWhenNoBkIndexExists() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "PurchaseOrderWithoutBusinessKey"),
                0,
                null);

        assertThat(entry.eligible()).isFalse();
        assertThat(entry.eligibilityReason()).contains("No unique index or unique constraint ending with _PK");
    }

    @Test
    void mapsIneligibleTableWhenMultipleBkIndexesExist() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "AmbiguousBusinessKey"),
                2,
                null);

        assertThat(entry.eligible()).isFalse();
        assertThat(entry.eligibilityReason()).contains("Multiple unique indexes or unique constraints ending with _PK");
    }

    @Test
    void mapsIneligibleTableWhenTableExtendedPropertyIsTruthy() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "Supplier"),
                1,
                "true");

        assertThat(entry.eligible()).isFalse();
        assertThat(entry.eligibilityReason()).contains("extended-property metadata");
    }

    @Test
    void nonTruthyTableExtendedPropertyDoesNotDisableTable() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "Supplier"),
                1,
                "false");

        assertThat(entry.eligible()).isTrue();
        assertThat(entry.eligibilityReason()).isNull();
    }
}
