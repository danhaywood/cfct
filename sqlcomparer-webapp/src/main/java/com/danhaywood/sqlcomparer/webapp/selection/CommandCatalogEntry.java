package com.danhaywood.cfct.webapp.selection;

public record CommandCatalogEntry(
        String interactionId,
        String logicalMemberIdentifier,
        String target,
        String replayState,
        String executeIn,
        String timestamp,
        boolean selected) {

    public CommandCatalogEntry withSelected(final boolean selected) {
        return new CommandCatalogEntry(
                interactionId,
                logicalMemberIdentifier,
                target,
                replayState,
                executeIn,
                timestamp,
                selected);
    }
}
