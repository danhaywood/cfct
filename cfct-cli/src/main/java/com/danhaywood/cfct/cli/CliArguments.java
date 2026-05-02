package com.danhaywood.cfct.cli;

import com.danhaywood.cfct.model.TableRef;

import java.nio.file.Path;
import java.util.List;

public record CliArguments(
        String server,
        String username,
        String password,
        String leftDatabase,
        String rightDatabase,
        List<TableRef> tables,
        CliOutputFormat outputFormat,
        Path outputFile
) {

    public CliArguments {
        tables = List.copyOf(tables);
        if (outputFormat == null) {
            outputFormat = CliOutputFormat.TEXT;
        }
    }
}
