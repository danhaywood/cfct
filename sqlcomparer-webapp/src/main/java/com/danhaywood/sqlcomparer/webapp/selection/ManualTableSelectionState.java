package com.danhaywood.sqlcomparer.webapp.selection;

import com.danhaywood.sqlcomparer.model.TableRef;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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

    public boolean isSelected(final TableRef table) {
        final TableCatalogEntry current = entriesByTable.get(table);
        return current != null && current.selected();
    }

    public int selectedCount() {
        return selectedTables().size();
    }

    public boolean isCompareEnabled() {
        return selectedCount() > 0;
    }

    public List<TableRef> selectedTables() {
        return entriesByTable.values().stream()
                .filter(TableCatalogEntry::selected)
                .map(TableCatalogEntry::table)
                .collect(Collectors.toList());
    }

    public List<TableCatalogEntry> filteredEntries(final String filter) {
        return entriesByTable.values().stream()
                .filter(entry -> matchesFilter(entry, filter))
                .collect(Collectors.toList());
    }

    public List<TableCatalogEntry> entriesSortedByTableName(final String filter, final boolean ascending) {
        final Comparator<TableCatalogEntry> comparator = Comparator.comparing(
                entry -> entry.table().displayName(),
                String.CASE_INSENSITIVE_ORDER);
        return filteredEntries(filter).stream()
                .sorted(ascending ? comparator : comparator.reversed())
                .collect(Collectors.toList());
    }

    public boolean matchesFilter(final TableCatalogEntry entry, final String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        final String normalized = filter.toLowerCase(Locale.ROOT);
        return entry.table().schemaName().toLowerCase(Locale.ROOT).contains(normalized)
                || entry.table().tableName().toLowerCase(Locale.ROOT).contains(normalized)
                || entry.table().displayName().toLowerCase(Locale.ROOT).contains(normalized);
    }

    public String feedbackText() {
        return "Selected tables: " + selectedCount();
    }
}
