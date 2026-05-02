package com.danhaywood.cfct.service;

import com.danhaywood.cfct.model.TableRef;

public record ComparisonProgressEvent(
        TableRef table,
        ComparisonProgressPhase phase,
        int completedTables,
        int totalTables,
        String message) {
}
