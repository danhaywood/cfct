package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.model.TableRef;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WebappComparisonPreparationServiceTest {

    @Test
    void preparesRequestFromSelectionPlanOutput() {
        final SelectionPlan plan = () -> List.of(
                new TableRef("dbo", "Supplier"),
                new TableRef("dbo", "PurchaseOrder"));
        final WebappComparisonPreparationService service = new WebappComparisonPreparationService(plan);

        assertThat(service.prepareRequest().tables()).containsExactly(
                new TableRef("dbo", "Supplier"),
                new TableRef("dbo", "PurchaseOrder"));
    }
}
