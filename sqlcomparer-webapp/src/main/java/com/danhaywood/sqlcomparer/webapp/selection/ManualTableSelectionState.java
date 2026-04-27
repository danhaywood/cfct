package com.danhaywood.sqlcomparer.webapp.selection;

import com.danhaywood.sqlcomparer.model.TableRef;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManualTableSelectionState {

    private final Map<TableRef, TableCatalogEntry> entriesByTable = new LinkedHashMap<>();

    public ManualTableSelectionState(final List<TableCatalogEntry> entries) {
        for (TableCatalogEntry entry : entries) {
            entriesByTable.put(entry.table(), entry);
        }
    }

    public void updateSelection(final TableRef table, final boolean selected) {
        final TableCatalogEntry current = entriesByTable.get(table);
        if (current == null || !current.eligible()) {
            return;
        }
        entriesByTable.put(table, new TableCatalogEntry(current.table(), true, null, selected));
    }

    public List<TableRef> selectedTables() {
        return entriesByTable.values().stream()
                .filter(TableCatalogEntry::selected)
                .map(TableCatalogEntry::table)
                .collect(Collectors.toList());
    }

    public String feedbackText() {
        return "Selected tables: " + selectedTables().size();
    }
}
