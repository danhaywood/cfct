package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.model.TableRef;

public record TableCatalogEntry(
        TableRef table,
        boolean eligible,
        String eligibilityReason,
        boolean selected) {

    public static TableCatalogEntry eligible(final TableRef table) {
        return new TableCatalogEntry(table, true, null, false);
    }

    public static TableCatalogEntry ineligible(final TableRef table, final String reason) {
        return new TableCatalogEntry(table, false, reason, false);
    }
}
