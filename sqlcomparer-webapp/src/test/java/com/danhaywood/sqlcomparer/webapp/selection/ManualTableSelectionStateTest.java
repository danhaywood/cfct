package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.model.TableRef;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ManualTableSelectionStateTest {

    @Test
    void updatesSelectedTablesForEligibleRowsOnly() {
        final TableRef eligible = new TableRef("dbo", "Supplier");
        final TableRef ineligible = new TableRef("dbo", "PurchaseOrderWithoutBusinessKey");
        final ManualTableSelectionState state = new ManualTableSelectionState(List.of(
                TableCatalogEntry.eligible(eligible),
                TableCatalogEntry.ineligible(ineligible, "No unique index ending with _PK.")));

        state.updateSelection(eligible, true);
        state.updateSelection(ineligible, true);

        assertThat(state.selectedTables()).containsExactly(eligible);
        assertThat(state.selectedCount()).isEqualTo(1);
        assertThat(state.feedbackText()).isEqualTo("Selected tables: 1");
        assertThat(state.isCompareEnabled()).isTrue();
    }

    @Test
    void compareIsDisabledUntilAnEligibleTableIsSelected() {
        final TableRef eligible = new TableRef("dbo", "Supplier");
        final ManualTableSelectionState state = new ManualTableSelectionState(List.of(TableCatalogEntry.eligible(eligible)));

        assertThat(state.isCompareEnabled()).isFalse();

        state.updateSelection(eligible, true);
        assertThat(state.isCompareEnabled()).isTrue();

        state.updateSelection(eligible, false);
        assertThat(state.isCompareEnabled()).isFalse();
    }

    @Test
    void filtersEntriesBySchemaTableNameOrDisplayName() {
        final ManualTableSelectionState state = new ManualTableSelectionState(List.of(
                TableCatalogEntry.eligible(new TableRef("dbo", "Supplier")),
                TableCatalogEntry.eligible(new TableRef("audit", "SupplierHistory")),
                TableCatalogEntry.eligible(new TableRef("dbo", "PurchaseOrder"))));

        assertThat(state.filteredEntries("supplier"))
                .extracting(entry -> entry.table().displayName())
                .containsExactly("dbo.Supplier", "audit.SupplierHistory");
        assertThat(state.filteredEntries("audit"))
                .extracting(entry -> entry.table().displayName())
                .containsExactly("audit.SupplierHistory");
    }

    @Test
    void sortsFilteredEntriesByDisplayName() {
        final ManualTableSelectionState state = new ManualTableSelectionState(List.of(
                TableCatalogEntry.eligible(new TableRef("dbo", "Supplier")),
                TableCatalogEntry.eligible(new TableRef("dbo", "Address")),
                TableCatalogEntry.eligible(new TableRef("dbo", "PurchaseOrder"))));

        assertThat(state.entriesSortedByTableName("dbo", true))
                .extracting(entry -> entry.table().displayName())
                .containsExactly("dbo.Address", "dbo.PurchaseOrder", "dbo.Supplier");
        assertThat(state.entriesSortedByTableName("dbo", false))
                .extracting(entry -> entry.table().displayName())
                .containsExactly("dbo.Supplier", "dbo.PurchaseOrder", "dbo.Address");
    }

    @Test
    void appliesProgrammaticSelectionAndKeepsManualSelectionWhenProgrammaticIsCleared() {
        final TableRef supplier = new TableRef("dbo", "Supplier");
        final TableRef product = new TableRef("dbo", "Product");
        final ManualTableSelectionState state = new ManualTableSelectionState(List.of(
                TableCatalogEntry.eligible(supplier),
                TableCatalogEntry.eligible(product)));

        state.updateSelection(supplier, true);
        state.applyProgrammaticSelections(Set.of(product));

        assertThat(state.selectedTables()).containsExactly(supplier, product);

        state.applyProgrammaticSelections(Set.of());

        assertThat(state.selectedTables()).containsExactly(supplier);
    }

    @Test
    void manualDeselectionOverridesProgrammaticSelection() {
        final TableRef supplier = new TableRef("dbo", "Supplier");
        final ManualTableSelectionState state = new ManualTableSelectionState(List.of(
                TableCatalogEntry.eligible(supplier)));

        state.applyProgrammaticSelections(Set.of(supplier));
        assertThat(state.isSelected(supplier)).isTrue();

        state.updateSelection(supplier, false);

        assertThat(state.isSelected(supplier)).isFalse();
    }

    @Test
    void clearsManualAndProgrammaticSelections() {
        final TableRef supplier = new TableRef("dbo", "Supplier");
        final TableRef product = new TableRef("dbo", "Product");
        final ManualTableSelectionState state = new ManualTableSelectionState(List.of(
                TableCatalogEntry.eligible(supplier),
                TableCatalogEntry.eligible(product)));

        state.updateSelection(supplier, true);
        state.applyProgrammaticSelections(Set.of(product));
        assertThat(state.selectedCount()).isEqualTo(2);

        state.clearSelections();

        assertThat(state.selectedCount()).isZero();
        assertThat(state.selectedTables()).isEmpty();
    }
}
