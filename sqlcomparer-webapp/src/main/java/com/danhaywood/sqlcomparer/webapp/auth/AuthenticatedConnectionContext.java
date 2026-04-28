package com.danhaywood.sqlcomparer.webapp.auth;

public record AuthenticatedConnectionContext(
        String server,
        String username,
        String password,
        String leftDatabase,
        String rightDatabase) {
}
