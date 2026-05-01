package com.danhaywood.sqlcomparer.webapp.selection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommandSelectionStateTest {

    @Test
    void updatesAndReturnsSelectedInteractionIdsInDeterministicOrder() {
        final CommandSelectionState state = new CommandSelectionState(List.of(
                new CommandCatalogEntry("111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "EXPORTED", "FOREGROUND", "2026-04-05T10:00:00.000", false),
                new CommandCatalogEntry("222", "supplier.Supplier#updateName", "supplier.Supplier:302", "EXPORTED", "FOREGROUND", "2026-04-05T10:30:00.000", false),
                new CommandCatalogEntry("333", "product.Product#changeStatus", "product.Product:701", "PENDING", "FOREGROUND", "2026-04-05T11:00:00.000", false)));

        state.updateSelection("111", true);
        state.updateSelection("333", true);

        assertThat(state.selectedInteractionIds()).containsExactly("111", "333");
        assertThat(state.isSelected("111")).isTrue();
        assertThat(state.isSelected("222")).isFalse();
    }

    @Test
    void matchesFilterByInteractionId() {
        final CommandSelectionState state = new CommandSelectionState(List.of());
        final CommandCatalogEntry entry = new CommandCatalogEntry(
                "11111111-1111-1111-1111-111111111111",
                "supplier.Supplier#registerProduct",
                "supplier.Supplier:301",
                "EXPORTED",
                "FOREGROUND",
                "2026-04-05T10:00:00.000",
                false);

        assertThat(state.matchesFilter(entry, "1111")).isTrue();
        assertThat(state.matchesFilter(entry, "2222")).isFalse();
        assertThat(state.matchesFilter(entry, "")).isTrue();
    }
}
