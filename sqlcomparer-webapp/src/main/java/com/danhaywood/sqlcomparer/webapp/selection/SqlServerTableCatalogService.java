package com.danhaywood.sqlcomparer.webapp.selection;

import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SqlServerTableCatalogService {

    private static final String PK_SUFFIX = "_PK";

    private final WebappDataSources dataSources;

    public SqlServerTableCatalogService(final WebappDataSources dataSources) {
        this.dataSources = dataSources;
    }

    public List<TableCatalogEntry> discoverTableCatalog() {
        final String sql = """
                SELECT s.name AS schema_name,
                       t.name AS table_name,
                       SUM(CASE WHEN i.is_unique = 1 AND i.name IS NOT NULL AND i.name LIKE ? THEN 1 ELSE 0 END) AS bk_index_count
                FROM sys.tables t
                JOIN sys.schemas s ON s.schema_id = t.schema_id
                LEFT JOIN sys.indexes i ON i.object_id = t.object_id
                WHERE t.is_ms_shipped = 0
                GROUP BY s.name, t.name
                ORDER BY s.name, t.name
                """;

        try (Connection jdbc = dataSources.left().getConnection();
             PreparedStatement statement = jdbc.prepareStatement(sql)) {
            statement.setString(1, "%" + PK_SUFFIX);
            try (ResultSet resultSet = statement.executeQuery()) {
                final List<TableCatalogEntry> rows = new ArrayList<>();
                while (resultSet.next()) {
                    final String schemaName = resultSet.getString("schema_name");
                    final String tableName = resultSet.getString("table_name");
                    final int bkIndexCount = resultSet.getInt("bk_index_count");
                    rows.add(mapDiscoveredTable(new TableRef(schemaName, tableName), bkIndexCount));
                }
                return rows;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to discover tables for manual selection.", ex);
        }
    }

    static TableCatalogEntry mapDiscoveredTable(final TableRef table, final int bkIndexCount) {
        if (bkIndexCount == 1) {
            return TableCatalogEntry.eligible(table);
        }
        if (bkIndexCount <= 0) {
            return TableCatalogEntry.ineligible(table, "No unique index ending with _PK.");
        }
        return TableCatalogEntry.ineligible(table, "Multiple unique indexes ending with _PK.");
    }
}
