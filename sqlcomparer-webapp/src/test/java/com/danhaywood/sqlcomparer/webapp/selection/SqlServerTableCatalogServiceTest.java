package com.danhaywood.sqlcomparer.webapp.selection;

import com.danhaywood.sqlcomparer.model.TableRef;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SqlServerTableCatalogServiceTest {

    @Test
    void mapsEligibleTableWhenExactlyOneBkIndexExists() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "Supplier"),
                1);

        assertThat(entry.eligible()).isTrue();
        assertThat(entry.eligibilityReason()).isNull();
    }

    @Test
    void mapsIneligibleTableWhenNoBkIndexExists() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "PurchaseOrderWithoutBusinessKey"),
                0);

        assertThat(entry.eligible()).isFalse();
        assertThat(entry.eligibilityReason()).contains("No unique index ending with _BK");
    }

    @Test
    void mapsIneligibleTableWhenMultipleBkIndexesExist() {
        final TableCatalogEntry entry = SqlServerTableCatalogService.mapDiscoveredTable(
                new TableRef("dbo", "AmbiguousBusinessKey"),
                2);

        assertThat(entry.eligible()).isFalse();
        assertThat(entry.eligibilityReason()).contains("Multiple unique indexes ending with _BK");
    }
}
