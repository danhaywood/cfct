package com.danhaywood.cfct.webapp.selection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandSelectionState {

    private final Map<String, CommandCatalogEntry> entriesByInteractionId = new LinkedHashMap<>();

    public CommandSelectionState(final List<CommandCatalogEntry> entries) {
        for (CommandCatalogEntry entry : entries) {
            entriesByInteractionId.put(entry.interactionId(), entry);
        }
    }

    public void updateSelection(final String interactionId, final boolean selected) {
        final CommandCatalogEntry current = entriesByInteractionId.get(interactionId);
        if (current == null) {
            return;
        }
        entriesByInteractionId.put(interactionId, current.withSelected(selected));
    }

    public boolean isSelected(final String interactionId) {
        final CommandCatalogEntry current = entriesByInteractionId.get(interactionId);
        return current != null && current.selected();
    }

    public List<String> selectedInteractionIds() {
        return entriesByInteractionId.values().stream()
                .filter(CommandCatalogEntry::selected)
                .map(CommandCatalogEntry::interactionId)
                .collect(Collectors.toList());
    }

    public int selectedCount() {
        return (int) entriesByInteractionId.values().stream()
                .filter(CommandCatalogEntry::selected)
                .count();
    }

    public void clearSelections() {
        for (Map.Entry<String, CommandCatalogEntry> entry : entriesByInteractionId.entrySet()) {
            if (entry.getValue().selected()) {
                entriesByInteractionId.put(entry.getKey(), entry.getValue().withSelected(false));
            }
        }
    }

    public boolean matchesFilter(
            final CommandCatalogEntry entry,
            final String memberIdFilter,
            final String interactionIdFilter) {
        return matches(entry.logicalMemberIdentifier(), memberIdFilter)
                && matches(entry.interactionId(), interactionIdFilter);
    }

    private boolean matches(final String value, final String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        if (value == null) {
            return false;
        }
        return value.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
    }
}
