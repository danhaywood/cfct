package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.model.TableRef;

import java.util.List;

public final class ExplicitSelectionPlan implements SelectionPlan {

    private final List<TableRef> tables;

    public ExplicitSelectionPlan(final List<TableRef> tables) {
        if (tables == null || tables.isEmpty()) {
            throw new IllegalArgumentException("At least one explicit table is required");
        }
        this.tables = List.copyOf(tables);
    }

    @Override
    public List<TableRef> resolveTables() {
        return tables;
    }
}
