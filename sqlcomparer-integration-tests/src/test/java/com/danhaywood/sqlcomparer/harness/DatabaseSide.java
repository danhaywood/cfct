package com.danhaywood.sqlcomparer.harness;

public enum DatabaseSide {
    LEFT("left_db"),
    RIGHT("right_db");

    private final String databaseName;

    DatabaseSide(final String databaseName) {
        this.databaseName = databaseName;
    }

    public String databaseName() {
        return databaseName;
    }
}
