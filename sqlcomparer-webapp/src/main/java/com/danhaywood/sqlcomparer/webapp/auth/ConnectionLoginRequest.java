package com.danhaywood.sqlcomparer.webapp.auth;

public record ConnectionLoginRequest(
        String server,
        String username,
        String password,
        String leftDatabase,
        String rightDatabase) {

    public AuthenticatedConnectionContext toAuthenticatedContext() {
        return new AuthenticatedConnectionContext(
                required(server, "Server"),
                required(username, "Username"),
                required(password, "Password"),
                required(leftDatabase, "Left database"),
                required(rightDatabase, "Right database"));
    }

    private String required(final String value, final String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }
}
