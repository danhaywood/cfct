package com.danhaywood.sqlcomparer.webapp.selection;

import com.danhaywood.sqlcomparer.model.TableRef;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ManualTableSelectionStateTest {

    @Test
    void updatesSelectedTablesForEligibleRowsOnly() {
        final TableRef eligible = new TableRef("dbo", "Supplier");
        final TableRef ineligible = new TableRef("dbo", "PurchaseOrderWithoutBusinessKey");
        final ManualTableSelectionState state = new ManualTableSelectionState(List.of(
                TableCatalogEntry.eligible(eligible),
                TableCatalogEntry.ineligible(ineligible, "No unique index ending with _BK.")));

        state.updateSelection(eligible, true);
        state.updateSelection(ineligible, true);

        assertThat(state.selectedTables()).containsExactly(eligible);
        assertThat(state.feedbackText()).isEqualTo("Selected tables: 1");
    }
}
