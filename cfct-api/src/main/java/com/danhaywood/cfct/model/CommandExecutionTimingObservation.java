package com.danhaywood.cfct.model;

import java.util.Comparator;

public record CommandExecutionTimingObservation(
        ExecutionTimingSide side,
        ExecutionTimingScope scope,
        String interactionId,
        String parentInteractionId,
        String logicalMemberIdentifier,
        String replayState,
        String startedAt,
        String completedAt,
        Long durationMillis) {

    private static final Comparator<String> NULL_SAFE_TEXT = Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER);

    public static Comparator<CommandExecutionTimingObservation> deterministicOrder() {
        return Comparator
                .comparing(CommandExecutionTimingObservation::logicalMemberIdentifier, NULL_SAFE_TEXT)
                .thenComparing(CommandExecutionTimingObservation::startedAt, NULL_SAFE_TEXT)
                .thenComparing(CommandExecutionTimingObservation::interactionId, NULL_SAFE_TEXT)
                .thenComparing(CommandExecutionTimingObservation::side, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(CommandExecutionTimingObservation::scope, Comparator.nullsFirst(Comparator.naturalOrder()));
    }
}
