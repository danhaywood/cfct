package com.danhaywood.sqlcomparer.webapp.validation;

import java.time.Instant;

public record ConnectionValidationStatus(
        ConnectionValidationState state,
        Instant recordedAt,
        String summary) {

    public static ConnectionValidationStatus ok(final String summary) {
        return new ConnectionValidationStatus(ConnectionValidationState.OK, Instant.now(), summary);
    }

    public static ConnectionValidationStatus failed(final String summary) {
        return new ConnectionValidationStatus(ConnectionValidationState.FAILED, Instant.now(), summary);
    }
}
