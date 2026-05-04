package com.danhaywood.cfct.webapp.auth;

public record AuthenticatedConnectionContext(
        String jdbcUrl,
        String jdbcDriver,
        String username,
        String password,
        String leftDatabase,
        String rightDatabase) {
}
