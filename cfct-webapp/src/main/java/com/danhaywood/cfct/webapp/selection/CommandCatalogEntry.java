package com.danhaywood.cfct.webapp.selection;

public record CommandCatalogEntry(
        String interactionId,
        String parentInteractionId,
        String logicalMemberIdentifier,
        String target,
        String replayState,
        String executeIn,
        String timestamp,
        String completedAt,
        boolean selected) {

    public CommandCatalogEntry(
            final String interactionId,
            final String logicalMemberIdentifier,
            final String target,
            final String replayState,
            final String executeIn,
            final String timestamp,
            final String completedAt,
            final boolean selected) {
        this(interactionId, null, logicalMemberIdentifier, target, replayState, executeIn, timestamp, completedAt, selected);
    }

    public CommandCatalogEntry withSelected(final boolean selected) {
        return new CommandCatalogEntry(
                interactionId,
                parentInteractionId,
                logicalMemberIdentifier,
                target,
                replayState,
                executeIn,
                timestamp,
                completedAt,
                selected);
    }
}
