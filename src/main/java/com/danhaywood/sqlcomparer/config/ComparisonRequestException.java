package com.danhaywood.sqlcomparer.config;

public final class ComparisonRequestException extends RuntimeException {

    public ComparisonRequestException(final String message) {
        super(message);
    }

    public ComparisonRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
