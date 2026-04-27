package com.danhaywood.sqlcomparer.cli;

import com.danhaywood.sqlcomparer.model.TableRef;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public final class CliArgumentsParser {

    private static final String SERVER = "-S";
    private static final String USER = "-U";
    private static final String PASSWORD = "-P";
    private static final String LEFT_DATABASE = "-l";
    private static final String RIGHT_DATABASE = "-r";
    private static final String TABLES = "-t";

    private static final List<String> REQUIRED_FLAGS = List.of(SERVER, USER, PASSWORD, LEFT_DATABASE, RIGHT_DATABASE, TABLES);

    public CliArguments parse(final String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Missing required arguments: -S, -U, -P, -l, -r, -t");
        }

        final Map<String, String> valuesByFlag = parseFlagValues(args);
        validateRequired(valuesByFlag);

        return new CliArguments(
                valuesByFlag.get(SERVER).trim(),
                valuesByFlag.get(USER).trim(),
                valuesByFlag.get(PASSWORD),
                valuesByFlag.get(LEFT_DATABASE).trim(),
                valuesByFlag.get(RIGHT_DATABASE).trim(),
                parseTables(valuesByFlag.get(TABLES)));
    }

    private Map<String, String> parseFlagValues(final String[] args) {
        final Map<String, String> valuesByFlag = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            final String token = args[i];
            if (!REQUIRED_FLAGS.contains(token)) {
                throw new IllegalArgumentException("Unknown argument: " + token);
            }
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException("Missing value for argument: " + token);
            }
            valuesByFlag.put(token, args[++i]);
        }
        return valuesByFlag;
    }

    private void validateRequired(final Map<String, String> valuesByFlag) {
        final List<String> missing = new ArrayList<>();
        for (String requiredFlag : REQUIRED_FLAGS) {
            final String value = valuesByFlag.get(requiredFlag);
            if (value == null || value.isBlank()) {
                missing.add(requiredFlag);
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing required arguments: " + String.join(", ", missing));
        }
    }

    private List<TableRef> parseTables(final String tableList) {
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
}
