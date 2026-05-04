package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.TableMetadata;

import java.util.List;
import java.util.stream.Collectors;

public final class SqlServerTableDiffQueryBuilder {

    public String buildQuery(final TableMetadata metadata, final String leftDatabaseName, final String rightDatabaseName) {
        final String leftTable = quoteTable(leftDatabaseName, metadata);
        final String rightTable = quoteTable(rightDatabaseName, metadata);
        final List<ColumnRef> keyColumns = metadata.keyColumns();
        final List<ColumnRef> comparedColumns = metadata.comparedColumns();

        final String leftBaseSelect = selectBaseColumns("l", keyColumns, comparedColumns);
        final String rightBaseSelect = selectBaseColumns("r", keyColumns, comparedColumns);
        final String plainKeys = quotedColumns(keyColumns);

        final String leftOnlySelect = "SELECT 'ONLY_IN_LEFT' AS diff_kind, "
                + keyProjectionForOutput("lk", keyColumns)
                + comparedProjection("l", comparedColumns, "l")
                + comparedNullProjection(comparedColumns, "r")
                + " FROM left_only_keys lk JOIN left_base l ON " + joinOn("lk", "l", keyColumns);

        final String rightOnlySelect = "SELECT 'ONLY_IN_RIGHT' AS diff_kind, "
                + keyProjectionForOutput("rk", keyColumns)
                + comparedNullProjection(comparedColumns, "l")
                + comparedProjection("r", comparedColumns, "r")
                + " FROM right_only_keys rk JOIN right_base r ON " + joinOn("rk", "r", keyColumns);

        final String differingSelect = "SELECT 'DIFFERENT' AS diff_kind, "
                + keyProjectionForOutput("dk", keyColumns)
                + comparedProjection("l", comparedColumns, "l")
                + comparedProjection("r", comparedColumns, "r")
                + " FROM differing_keys dk"
                + " JOIN left_base l ON " + joinOn("dk", "l", keyColumns)
                + " JOIN right_base r ON " + joinOn("dk", "r", keyColumns);

        return "WITH left_base AS (SELECT " + leftBaseSelect + " FROM " + leftTable + " l), "
                + "right_base AS (SELECT " + rightBaseSelect + " FROM " + rightTable + " r), "
                + "left_only_keys AS (SELECT " + plainKeys + " FROM left_base EXCEPT SELECT " + plainKeys + " FROM right_base), "
                + "right_only_keys AS (SELECT " + plainKeys + " FROM right_base EXCEPT SELECT " + plainKeys + " FROM left_base), "
                + "differing_keys AS (SELECT " + aliasedColumnsWithoutAliases("l", keyColumns)
                + " FROM left_base l JOIN right_base r ON " + joinOn("l", "r", keyColumns)
                + " WHERE " + differencePredicate("l", "r", comparedColumns) + ") "
                + leftOnlySelect
                + " UNION ALL "
                + rightOnlySelect
                + " UNION ALL "
                + differingSelect;
    }

    private String quoteTable(final String databaseName, final TableMetadata metadata) {
        return quote(databaseName) + "." + SqlServerIdentifiers.quoteTable(metadata.table());
    }

    private String selectBaseColumns(
            final String tableAlias,
            final List<ColumnRef> keyColumns,
            final List<ColumnRef> comparedColumns) {
        final String keyProjection = aliasedColumnsWithAliases(tableAlias, keyColumns);
        final String comparedProjection = aliasedColumnsWithAliases(tableAlias, comparedColumns);
        if (comparedProjection.isEmpty()) {
            return keyProjection;
        }
        return keyProjection + ", " + comparedProjection;
    }

    private String keyProjectionForOutput(final String alias, final List<ColumnRef> keyColumns) {
        return aliasedColumnsWithAliases(alias, keyColumns);
    }

    private String aliasedColumnsWithAliases(final String alias, final List<ColumnRef> columns) {
        return columns.stream()
                .map(column -> aliasedColumn(alias, column) + " AS " + SqlServerIdentifiers.quoteColumn(column))
                .collect(Collectors.joining(", "));
    }

    private String aliasedColumnsWithoutAliases(final String alias, final List<ColumnRef> columns) {
        return columns.stream()
                .map(column -> aliasedColumn(alias, column))
                .collect(Collectors.joining(", "));
    }

    private String quotedColumns(final List<ColumnRef> columns) {
        return columns.stream()
                .map(SqlServerIdentifiers::quoteColumn)
                .collect(Collectors.joining(", "));
    }

    private String comparedProjection(final String sourceAlias, final List<ColumnRef> comparedColumns, final String outputPrefix) {
        if (comparedColumns.isEmpty()) {
            return "";
        }
        return comparedColumns.stream()
                .map(column -> ", " + aliasedColumn(sourceAlias, column) + " AS " + quote(outputPrefix + "_" + column.name()))
                .collect(Collectors.joining());
    }

    private String comparedNullProjection(final List<ColumnRef> comparedColumns, final String outputPrefix) {
        if (comparedColumns.isEmpty()) {
            return "";
        }
        return comparedColumns.stream()
                .map(column -> ", CAST(NULL AS NVARCHAR(MAX)) AS " + quote(outputPrefix + "_" + column.name()))
                .collect(Collectors.joining());
    }

    private String joinOn(final String leftAlias, final String rightAlias, final List<ColumnRef> keyColumns) {
        return keyColumns.stream()
                .map(column -> aliasedColumn(leftAlias, column) + " = " + aliasedColumn(rightAlias, column))
                .collect(Collectors.joining(" AND "));
    }

    private String differencePredicate(final String leftAlias, final String rightAlias, final List<ColumnRef> comparedColumns) {
        if (comparedColumns.isEmpty()) {
            return "1 = 0";
        }
        return comparedColumns.stream()
                .map(column -> "(" + aliasedColumn(leftAlias, column) + " <> " + aliasedColumn(rightAlias, column)
                        + " OR (" + aliasedColumn(leftAlias, column) + " IS NULL AND " + aliasedColumn(rightAlias, column) + " IS NOT NULL)"
                        + " OR (" + aliasedColumn(leftAlias, column) + " IS NOT NULL AND " + aliasedColumn(rightAlias, column) + " IS NULL))")
                .collect(Collectors.joining(" OR "));
    }

    private String aliasedColumn(final String alias, final ColumnRef column) {
        return alias + "." + SqlServerIdentifiers.quoteColumn(column);
    }

    private String quote(final String identifier) {
        return "[" + identifier.replace("]", "]]" ) + "]";
    }
}
