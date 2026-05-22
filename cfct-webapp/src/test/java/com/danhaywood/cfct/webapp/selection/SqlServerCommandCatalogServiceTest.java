package com.danhaywood.cfct.webapp.selection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SqlServerCommandCatalogServiceTest {

    @Test
    void mapsDiscoveredCommandRowWithNullCompletedAt() {
        final CommandCatalogEntry entry = SqlServerCommandCatalogService.mapDiscoveredCommand(
                "11111111-1111-1111-1111-111111111111",
                "supplier.Supplier#registerProduct",
                "supplier.Supplier:301",
                "EXPORTED",
                "FOREGROUND",
                "2026-04-05T10:00:00.000",
                null);

        assertThat(entry.completedAt()).isNull();
    }

    @Test
    void mapsDiscoveredCommandRowWithBlankCompletedAt() {
        final CommandCatalogEntry entry = SqlServerCommandCatalogService.mapDiscoveredCommand(
                "11111111-1111-1111-1111-111111111111",
                "supplier.Supplier#registerProduct",
                "supplier.Supplier:301",
                "EXPORTED",
                "FOREGROUND",
                "2026-04-05T10:00:00.000",
                "");

        assertThat(entry.completedAt()).isEmpty();
    }


    @Test
    void mapsDiscoveredCommandRowToCatalogEntry() {
        final CommandCatalogEntry entry = SqlServerCommandCatalogService.mapDiscoveredCommand(
                "11111111-1111-1111-1111-111111111111",
                "supplier.Supplier#registerProduct",
                "supplier.Supplier:301",
                "EXPORTED",
                "FOREGROUND",
                "2026-04-05T10:00:00.000",
                "2026-04-05T10:01:02.000");

        assertThat(entry.interactionId()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(entry.logicalMemberIdentifier()).isEqualTo("supplier.Supplier#registerProduct");
        assertThat(entry.target()).isEqualTo("supplier.Supplier:301");
        assertThat(entry.replayState()).isEqualTo("EXPORTED");
        assertThat(entry.completedAt()).isEqualTo("2026-04-05T10:01:02.000");
        assertThat(entry.selected()).isFalse();
    }
}
