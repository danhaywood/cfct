package com.danhaywood.cfct.cli;

import com.danhaywood.cfct.model.TableRef;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public record CliArguments(
        String server,
        String username,
        String password,
        String leftDatabase,
        String rightDatabase,
        List<TableRef> tables,
        CliOutputFormat outputFormat,
        Path outputFile,
        LocalDateTime commandsFrom,
        LocalDateTime commandsTo
) {

    public CliArguments(
            final String server,
            final String username,
            final String password,
            final String leftDatabase,
            final String rightDatabase,
            final List<TableRef> tables,
            final CliOutputFormat outputFormat,
            final Path outputFile) {
        this(server, username, password, leftDatabase, rightDatabase, tables, outputFormat, outputFile, null, null);
    }

    public CliArguments {
        tables = tables == null ? List.of() : List.copyOf(tables);
        if (outputFormat == null) {
            outputFormat = CliOutputFormat.TEXT;
        }
    }

    public boolean usesCommandTimeRangeSelection() {
        return commandsFrom != null || commandsTo != null;
    }
}
