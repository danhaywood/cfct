package com.danhaywood.cfct.model;

public enum DiffRowKind {
    ONLY_IN_LEFT,
    ONLY_IN_RIGHT,
    DIFFERENT;

    public static DiffRowKind fromSqlMarker(final String marker) {
        return switch (marker) {
            case "ONLY_IN_LEFT" -> ONLY_IN_LEFT;
            case "ONLY_IN_RIGHT" -> ONLY_IN_RIGHT;
            case "DIFFERENT" -> DIFFERENT;
            default -> throw new IllegalArgumentException("Unknown diff row marker: " + marker);
        };
    }
}
