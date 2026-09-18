package com.danhaywood.cfct.model;

public record ExecutionTimingResult(
        ForegroundExecutionTiming foreground,
        BackgroundExecutionTiming background) {
}
