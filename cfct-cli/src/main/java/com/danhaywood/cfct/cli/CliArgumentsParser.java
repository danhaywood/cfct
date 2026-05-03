package com.danhaywood.cfct.cli;

import com.danhaywood.cfct.model.TableRef;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
public final class CliArgumentsParser {

    private static final String SERVER = "-S";
    private static final String USER = "-U";
    private static final String PASSWORD = "-P";
    private static final String LEFT_DATABASE = "-l";
    private static final String RIGHT_DATABASE = "-r";

    private static final String ENV_SERVER = "SQLCOMPARER_SERVER";
    private static final String ENV_USER = "SQLCOMPARER_USERNAME";
    private static final String ENV_PASSWORD = "SQLCOMPARER_PASSWORD";
    private static final String ENV_LEFT_DATABASE = "SQLCOMPARER_LEFT_DATABASE";
    private static final String ENV_RIGHT_DATABASE = "SQLCOMPARER_RIGHT_DATABASE";

    private static final List<String> CONNECTION_FLAGS = List.of(SERVER, USER, PASSWORD, LEFT_DATABASE, RIGHT_DATABASE);

    public CliArguments parse(final String[] args) {
        final CliParseOptions options = parseOptions(args == null ? new String[0] : args);
        final Map<String, String> valuesByEnvKey = loadEnvValues(options.envFile);
        final Map<String, String> resolvedValues = resolveConnectionValues(options, valuesByEnvKey);
        validateRequiredConnections(resolvedValues);

        final CliOutputFormat outputFormat = CliOutputFormat.parse(options.outputFormat);
        final Path outputFile = parseOutputFile(options.outputFile);
        validateOutputDestination(outputFormat, outputFile);

        final SelectionMode selectionMode = parseSelectionMode(options);
        return new CliArguments(
                resolvedValues.get(SERVER).trim(),
                resolvedValues.get(USER).trim(),
                resolvedValues.get(PASSWORD),
                resolvedValues.get(LEFT_DATABASE).trim(),
                resolvedValues.get(RIGHT_DATABASE).trim(),
                selectionMode.tables(),
                outputFormat,
                outputFile,
                selectionMode.commandsFrom(),
                selectionMode.commandsTo());
    }

    private CliParseOptions parseOptions(final String[] args) {
        final CliParseOptions options = new CliParseOptions();
        try {
            new CommandLine(options).parseArgs(args);
            return options;
        } catch (CommandLine.ParameterException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private Map<String, String> loadEnvValues(final String explicitEnvFile) {
        final Path envPath = explicitEnvFile == null || explicitEnvFile.isBlank()
                ? Path.of(System.getProperty("user.dir"), ".env")
                : Path.of(explicitEnvFile.trim());
        if (!Files.exists(envPath)) {
            if (explicitEnvFile == null || explicitEnvFile.isBlank()) {
                return Map.of();
            }
            throw new IllegalArgumentException("Environment file not found: " + envPath);
        }
        if (!Files.isRegularFile(envPath)) {
            throw new IllegalArgumentException("Environment file is not a regular file: " + envPath);
        }
        try {
            return parseEnvLines(Files.readAllLines(envPath, StandardCharsets.UTF_8), envPath);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to read environment file: " + envPath, ex);
        }
    }

    private Map<String, String> parseEnvLines(final List<String> lines, final Path envPath) {
        final Map<String, String> valuesByEnvKey = new HashMap<>();
        for (int index = 0; index < lines.size(); index++) {
            final String line = lines.get(index).trim();
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            final int separator = line.indexOf('=');
            if (separator <= 0) {
                throw new IllegalArgumentException("Invalid .env entry at %s:%d. Expected KEY=value".formatted(envPath, index + 1));
            }
            final String key = line.substring(0, separator).trim();
            final String value = line.substring(separator + 1).trim();
            valuesByEnvKey.put(key, value);
        }
        return valuesByEnvKey;
    }

    private Map<String, String> resolveConnectionValues(final CliParseOptions options, final Map<String, String> valuesByEnvKey) {
        final Map<String, String> resolvedValues = new HashMap<>();
        putResolvedValue(resolvedValues, options.server, valuesByEnvKey, SERVER, ENV_SERVER);
        putResolvedValue(resolvedValues, options.username, valuesByEnvKey, USER, ENV_USER);
        putResolvedValue(resolvedValues, options.password, valuesByEnvKey, PASSWORD, ENV_PASSWORD);
        putResolvedValue(resolvedValues, options.leftDatabase, valuesByEnvKey, LEFT_DATABASE, ENV_LEFT_DATABASE);
        putResolvedValue(resolvedValues, options.rightDatabase, valuesByEnvKey, RIGHT_DATABASE, ENV_RIGHT_DATABASE);
        return resolvedValues;
    }

    private void putResolvedValue(
            final Map<String, String> resolvedValues,
            final String commandLineValue,
            final Map<String, String> valuesByEnvKey,
            final String flag,
            final String envKey) {
        if (commandLineValue != null && !commandLineValue.isBlank()) {
            resolvedValues.put(flag, commandLineValue);
            return;
        }
        final String envValue = valuesByEnvKey.get(envKey);
        if (envValue != null && !envValue.isBlank()) {
            resolvedValues.put(flag, envValue);
        }
    }

    private void validateRequiredConnections(final Map<String, String> resolvedValues) {
        final List<String> missing = new ArrayList<>();
        for (String requiredFlag : CONNECTION_FLAGS) {
            final String value = resolvedValues.get(requiredFlag);
            if (value == null || value.isBlank()) {
                missing.add(requiredFlag);
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing required arguments: " + String.join(", ", missing));
        }
    }

    private Path parseOutputFile(final String outputFile) {
        if (outputFile == null || outputFile.isBlank()) {
            return null;
        }
        return Path.of(outputFile.trim());
    }

    private void validateOutputDestination(final CliOutputFormat outputFormat, final Path outputFile) {
        if (outputFormat == CliOutputFormat.EXCEL && outputFile == null) {
            throw new IllegalArgumentException("Missing required argument: -o is required for excel output");
        }
    }

    private SelectionMode parseSelectionMode(final CliParseOptions options) {
        final boolean hasInlineTables = options.tables != null;
        final boolean hasTablesFile = options.tablesFile != null;
        final boolean hasExplicitTableMode = hasInlineTables || hasTablesFile;
        final boolean hasCommandsFrom = options.commandsFrom != null;
        final boolean hasCommandsTo = options.commandsTo != null;
        final boolean hasCommandRangeMode = hasCommandsFrom || hasCommandsTo;

        if (hasInlineTables && hasTablesFile) {
            throw new IllegalArgumentException("Only one explicit table source is allowed: use either -t or --tables-file");
        }
        if (hasExplicitTableMode && hasCommandRangeMode) {
            throw new IllegalArgumentException("Only one table-selection mode is allowed: use explicit table input (-t/--tables-file) or command-time-range (--commands-from/--commands-to)");
        }
        if (!hasExplicitTableMode && !hasCommandRangeMode) {
            throw new IllegalArgumentException("Missing required arguments: provide -t/--tables-file or --commands-from with --commands-to");
        }

        if (hasExplicitTableMode) {
            if (options.tablesFile != null) {
                return new SelectionMode(parseTablesFile(options.tablesFile), null, null);
            }
            return new SelectionMode(parseTables(options.tables), null, null);
        }

        if (!hasCommandsFrom || !hasCommandsTo) {
            throw new IllegalArgumentException("Both --commands-from and --commands-to are required for command-time-range selection");
        }
        final LocalDateTime commandsFrom = parseTimestamp(options.commandsFrom, "--commands-from");
        final LocalDateTime commandsTo = parseTimestamp(options.commandsTo, "--commands-to");
        if (commandsTo.isBefore(commandsFrom)) {
            throw new IllegalArgumentException("Invalid command-time-range: --commands-to must be greater than or equal to --commands-from");
        }
        return new SelectionMode(List.of(), commandsFrom, commandsTo);
    }

    private LocalDateTime parseTimestamp(final String raw, final String argumentName) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Missing value for argument: " + argumentName);
        }
        try {
            return LocalDateTime.parse(raw.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid timestamp for %s: '%s'. Expected ISO-8601 local date-time (e.g. 2026-05-01T12:30:00)".formatted(argumentName, raw), ex);
        }
    }

    private List<TableRef> parseTables(final String tableList) {
        if (tableList == null || tableList.isBlank()) {
            throw new IllegalArgumentException("At least one table is required in -t");
        }
        final String[] tokens = tableList.split(",", -1);
        final List<TableRef> tables = new ArrayList<>();
        for (String token : tokens) {
            final String tableToken = token.trim();
            if (tableToken.isBlank()) {
                throw new IllegalArgumentException("Invalid table reference: blank token in -t list");
            }
            tables.add(parseTableToken(tableToken));
        }
        if (tables.isEmpty()) {
            throw new IllegalArgumentException("At least one table is required in -t");
        }
        return List.copyOf(tables);
    }

    private List<TableRef> parseTablesFile(final String tableFile) {
        if (tableFile == null || tableFile.isBlank()) {
            throw new IllegalArgumentException("Missing value for argument: --tables-file");
        }
        final Path tableFilePath = Path.of(tableFile.trim());
        if (!Files.exists(tableFilePath)) {
            throw new IllegalArgumentException("Tables file not found: " + tableFilePath);
        }
        if (!Files.isRegularFile(tableFilePath)) {
            throw new IllegalArgumentException("Tables file is not a regular file: " + tableFilePath);
        }
        final List<String> lines;
        try {
            lines = Files.readAllLines(tableFilePath, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to read tables file: " + tableFilePath, ex);
        }
        final List<TableRef> tables = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            final String tableToken = lines.get(index).trim();
            if (tableToken.isBlank()) {
                throw new IllegalArgumentException("Invalid table reference: blank line in --tables-file at line " + (index + 1));
            }
            tables.add(parseTableToken(tableToken));
        }
        if (tables.isEmpty()) {
            throw new IllegalArgumentException("At least one table is required in --tables-file");
        }
        return List.copyOf(tables);
    }

    private TableRef parseTableToken(final String token) {
        final int separator = token.indexOf('.');
        if (separator <= 0 || separator != token.lastIndexOf('.') || separator == token.length() - 1) {
            throw new IllegalArgumentException("Invalid table reference '%s'. Expected schema.table".formatted(token));
        }

        final String schemaName = token.substring(0, separator).trim();
        final String tableName = token.substring(separator + 1).trim();
        if (schemaName.isBlank() || tableName.isBlank()) {
            throw new IllegalArgumentException("Invalid table reference '%s'. Expected schema.table".formatted(token));
        }
        return new TableRef(schemaName, tableName);
    }

    @Command(name = "cfct", sortOptions = false)
    private static final class CliParseOptions {

        @Option(names = {"-S", "--server"})
        private String server;

        @Option(names = {"-U", "--username"})
        private String username;

        @Option(names = {"-P", "--password"})
        private String password;

        @Option(names = {"-l", "--left-database"})
        private String leftDatabase;

        @Option(names = {"-r", "--right-database"})
        private String rightDatabase;

        @Option(names = {"-t", "--tables"})
        private String tables;

        @Option(names = {"-F", "--tables-file"})
        private String tablesFile;

        @Option(names = {"-e", "--env-file"})
        private String envFile;

        @Option(names = {"--commands-from"})
        private String commandsFrom;

        @Option(names = {"--commands-to"})
        private String commandsTo;

        @Option(names = {"-f", "--output-format"})
        private String outputFormat;

        @Option(names = {"-o", "--output-file"})
        private String outputFile;
    }

    private record SelectionMode(List<TableRef> tables, LocalDateTime commandsFrom, LocalDateTime commandsTo) {
    }
}
