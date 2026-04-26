package com.danhaywood.sqlcomparer.core;

public final class MetadataException extends RuntimeException {

    public MetadataException(final String message) {
        super(message);
    }

    public MetadataException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
