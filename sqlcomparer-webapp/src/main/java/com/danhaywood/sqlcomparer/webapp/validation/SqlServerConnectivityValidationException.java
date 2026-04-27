package com.danhaywood.sqlcomparer.webapp.validation;

public class SqlServerConnectivityValidationException extends RuntimeException {

    public SqlServerConnectivityValidationException(final String message) {
        super(message);
    }

    public SqlServerConnectivityValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
