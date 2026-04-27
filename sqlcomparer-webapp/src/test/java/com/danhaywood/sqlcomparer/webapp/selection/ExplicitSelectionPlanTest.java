package com.danhaywood.sqlcomparer.webapp.selection;

import com.danhaywood.sqlcomparer.model.TableRef;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExplicitSelectionPlanTest {

    @Test
    void resolvesTablesInDeterministicConfiguredOrder() {
        final ExplicitSelectionPlan plan = new ExplicitSelectionPlan(List.of(
                new TableRef("dbo", "Supplier"),
                new TableRef("dbo", "PurchaseOrder")));

        assertThat(plan.resolveTables()).containsExactly(
                new TableRef("dbo", "Supplier"),
                new TableRef("dbo", "PurchaseOrder"));
    }

    @Test
    void rejectsEmptyTableList() {
        assertThatThrownBy(() -> new ExplicitSelectionPlan(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one explicit table is required");
    }
}
