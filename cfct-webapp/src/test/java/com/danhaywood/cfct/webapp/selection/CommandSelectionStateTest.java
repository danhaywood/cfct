package com.danhaywood.cfct.webapp.selection;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CommandSelectionStateTest {

    @Test
    void updatesAndReturnsSelectedInteractionIdsInDeterministicOrder() {
        final CommandSelectionState state = new CommandSelectionState(List.of(
                new CommandCatalogEntry("111", "supplier.Supplier#registerProduct", "supplier.Supplier:301", "EXPORTED", "FOREGROUND", "2026-04-05T10:00:00.000", null, false),
                new CommandCatalogEntry("222", "supplier.Supplier#updateName", "supplier.Supplier:302", "EXPORTED", "FOREGROUND", "2026-04-05T10:30:00.000", null, false),
                new CommandCatalogEntry("333", "product.Product#changeStatus", "product.Product:701", "PENDING", "FOREGROUND", "2026-04-05T11:00:00.000", null, false)));

        state.updateSelection("111", true);
        state.updateSelection("333", true);

        assertThat(state.selectedInteractionIds()).containsExactly("111", "333");
        assertThat(state.isSelected("111")).isTrue();
        assertThat(state.isSelected("222")).isFalse();
    }

    @Test
    void matchesFilterByMemberInteractionAndReplayState() {
        final CommandSelectionState state = new CommandSelectionState(List.of());
        final CommandCatalogEntry entry = new CommandCatalogEntry(
                "11111111-1111-1111-1111-111111111111",
                "supplier.Supplier#registerProduct",
                "supplier.Supplier:301",
                "FAILED",
                "FOREGROUND",
                "2026-04-05T10:00:00.000",
                null,
                false);

        assertThat(state.matchesFilter(entry, "supplier", "1111", Set.of(), null)).isTrue();
        assertThat(state.matchesFilter(entry, "invoice", "1111", Set.of(), null)).isFalse();
        assertThat(state.matchesFilter(entry, "supplier", "2222", Set.of(), null)).isFalse();
        assertThat(state.matchesFilter(entry, "supplier", "1111", Set.of("FAILED"), null)).isTrue();
        assertThat(state.matchesFilter(entry, "supplier", "1111", Set.of("PENDING"), null)).isFalse();
        assertThat(state.matchesFilter(entry, "", "", Set.of(), null)).isTrue();
    }

    @Test
    void appliesInclusiveBaselineFiltering() {
        final CommandSelectionState state = new CommandSelectionState(List.of());
        final CommandCatalogEntry entry = new CommandCatalogEntry(
                "11111111-1111-1111-1111-111111111111",
                "supplier.Supplier#registerProduct",
                "supplier.Supplier:301",
                "FAILED",
                "FOREGROUND",
                "2026-04-05T10:00:00.000",
                null,
                false);

        assertThat(state.matchesFilter(entry, "", "", Set.of(), LocalDateTime.parse("2026-04-05T09:59:59.999"))).isTrue();
        assertThat(state.matchesFilter(entry, "", "", Set.of(), LocalDateTime.parse("2026-04-05T10:00:00.000"))).isTrue();
        assertThat(state.matchesFilter(entry, "", "", Set.of(), LocalDateTime.parse("2026-04-05T10:00:00.001"))).isFalse();
    }

    @Test
    void clearsAllSelectedCommands() {
        final CommandSelectionState state = new CommandSelectionState(List.of(
                new CommandCatalogEntry("111", "a", "a", "EXPORTED", "FOREGROUND", "2026-04-05T10:00:00.000", null, true),
                new CommandCatalogEntry("222", "b", "b", "EXPORTED", "FOREGROUND", "2026-04-05T10:01:00.000", null, false)));

        assertThat(state.selectedCount()).isEqualTo(1);

        state.clearSelections();

        assertThat(state.selectedCount()).isZero();
        assertThat(state.selectedInteractionIds()).isEmpty();
    }
}
