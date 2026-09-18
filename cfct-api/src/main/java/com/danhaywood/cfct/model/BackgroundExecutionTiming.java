package com.danhaywood.cfct.model;

import java.util.List;

public record BackgroundExecutionTiming(
        List<CommandExecutionTimingObservation> appA,
        List<CommandExecutionTimingObservation> appB) {

    public BackgroundExecutionTiming {
        appA = sortedCopy(appA);
        appB = sortedCopy(appB);
    }

    private static List<CommandExecutionTimingObservation> sortedCopy(
            final List<CommandExecutionTimingObservation> observations) {
        if (observations == null || observations.isEmpty()) {
            return List.of();
        }
        return observations.stream()
                .sorted(CommandExecutionTimingObservation.deterministicOrder())
                .toList();
    }
}
