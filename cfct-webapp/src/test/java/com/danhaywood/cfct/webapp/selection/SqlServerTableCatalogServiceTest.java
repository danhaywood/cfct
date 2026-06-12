package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.model.TableRef;

import org.junit.jupiter.api.Test;

import java.util.List;

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
                List.of(new SqlServerTableCatalogService.KeyCandidate("Supplier_PK", false)),
                null);

        assertThat(entry.eligible()).isTrue();
        assertThat(entry.eligibilityReason()).isNull();
    }

    @Test
    void mapsIneligibleTableWhenNoBkIndexExists() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "PurchaseOrderWithoutBusinessKey"),
                List.of(),
                null);

        assertThat(entry.eligible()).isFalse();
        assertThat(entry.eligibilityReason()).contains("No unique index or unique constraint ending with _PK");
    }

    @Test
    void mapsIneligibleTableWhenMultipleBkIndexesExist() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "AmbiguousBusinessKey"),
                List.of(
                        new SqlServerTableCatalogService.KeyCandidate("AmbiguousBusinessKey_PK", false),
                        new SqlServerTableCatalogService.KeyCandidate("AmbiguousBusinessKeyExternal_PK", false)),
                null);

        assertThat(entry.eligible()).isFalse();
        assertThat(entry.eligibilityReason()).contains("Multiple unique indexes or unique constraints ending with _PK");
    }

    @Test
    void mapsEligibleTableWhenPrimaryKeyDisambiguatesMultipleBkIndexes() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "ApplicationUser"),
                List.of(
                        new SqlServerTableCatalogService.KeyCandidate("ApplicationUser_PK", true),
                        new SqlServerTableCatalogService.KeyCandidate("ApplicationUser__username__PK", false)),
                null);

        assertThat(entry.eligible()).isTrue();
        assertThat(entry.eligibilityReason()).isNull();
    }

    @Test
    void mapsEligibleTableWhenUnrelatedUniqueConstraintExists() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "ApplicationUser"),
                List.of(new SqlServerTableCatalogService.KeyCandidate("ApplicationUser_PK", true)),
                null);

        assertThat(entry.eligible()).isTrue();
        assertThat(entry.eligibilityReason()).isNull();
    }

    @Test
    void mapsEligibleTableWhenTableExtendedPropertyIsNotTruthy() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "Supplier"),
                List.of(new SqlServerTableCatalogService.KeyCandidate("Supplier_PK", false)),
                "description");

        assertThat(entry.eligible()).isTrue();
        assertThat(entry.eligibilityReason()).isNull();
    }

    @Test
    void matchesBusinessKeySuffixLiterally() {
        assertThat(SqlServerTableCatalogService.hasBusinessKeySuffix("Supplier_PK")).isTrue();
        assertThat(SqlServerTableCatalogService.hasBusinessKeySuffix("SupplierXPK")).isFalse();
        assertThat(SqlServerTableCatalogService.hasBusinessKeySuffix("Supplier_pk")).isTrue();
    }

    @Test
    void mapsIneligibleTableWhenTableExtendedPropertyIsTruthy() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "Supplier"),
                List.of(new SqlServerTableCatalogService.KeyCandidate("Supplier_PK", false)),
                "true");

        assertThat(entry.eligible()).isFalse();
        assertThat(entry.eligibilityReason()).contains("extended-property metadata");
    }

    @Test
    void nonTruthyTableExtendedPropertyDoesNotDisableTable() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "Supplier"),
                List.of(new SqlServerTableCatalogService.KeyCandidate("Supplier_PK", false)),
                "false");

        assertThat(entry.eligible()).isTrue();
        assertThat(entry.eligibilityReason()).isNull();
    }
}
