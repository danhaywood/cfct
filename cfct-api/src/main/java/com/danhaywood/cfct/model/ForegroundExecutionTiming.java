package com.danhaywood.cfct.model;

public record ForegroundExecutionTiming(
        CommandExecutionTimingObservation appA,
        CommandExecutionTimingObservation appB) {
}
