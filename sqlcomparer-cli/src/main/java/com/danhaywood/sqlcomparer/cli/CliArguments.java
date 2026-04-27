package com.danhaywood.sqlcomparer.cli;

import com.danhaywood.sqlcomparer.model.TableRef;

import java.util.List;

public record CliArguments(
        String server,
        String username,
        String password,
        String leftDatabase,
        String rightDatabase,
        List<TableRef> tables
) {

    public CliArguments {
        tables = List.copyOf(tables);
    }
}
