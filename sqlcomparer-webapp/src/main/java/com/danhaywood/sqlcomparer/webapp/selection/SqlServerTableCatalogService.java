package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContextHolder;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.cfct.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SqlServerTableCatalogService {

    private static final String PK_SUFFIX = "_PK";
    private static final Set<TableRef> EXCLUDED_SUPPORT_TABLES = Set.of(
            new TableRef("causewayExtCommandLog", "CommandLogEntry"),
            new TableRef("causewayExtAuditTrail", "AuditTrailEntry"),
            new TableRef("util", "LogicalTypeTableMapping"));

    private final WebappDataSourceConfiguration dataSourceConfiguration;
    private final AuthenticatedConnectionContextHolder authenticatedContextHolder;

    public SqlServerTableCatalogService(
            final WebappDataSourceConfiguration dataSourceConfiguration,
            final AuthenticatedConnectionContextHolder authenticatedContextHolder) {
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.authenticatedContextHolder = authenticatedContextHolder;
    }

    public List<TableCatalogEntry> discoverTableCatalog() {
        final WebappDataSources dataSources = dataSourceConfiguration.dataSourcesFor(authenticatedContextHolder.required());
        final String sql = """
                SELECT s.name AS schema_name,
                       t.name AS table_name,
                       SUM(CASE WHEN i.is_unique = 1 AND i.name IS NOT NULL AND i.name LIKE ? THEN 1 ELSE 0 END) AS bk_key_object_count
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
                    final TableRef table = new TableRef(schemaName, tableName);
                    if (isExcludedTable(table)) {
                        continue;
                    }
                    final int bkKeyObjectCount = resultSet.getInt("bk_key_object_count");
                    rows.add(mapDiscoveredTable(table, bkKeyObjectCount));
                }
                return rows;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to discover tables for manual selection.", ex);
        }
    }

    static boolean isExcludedTable(final TableRef table) {
        return EXCLUDED_SUPPORT_TABLES.contains(table);
    }

    static TableCatalogEntry mapDiscoveredTable(final TableRef table, final int bkKeyObjectCount) {
        if (bkKeyObjectCount == 1) {
            return TableCatalogEntry.eligible(table);
        }
        if (bkKeyObjectCount <= 0) {
            return TableCatalogEntry.ineligible(table, "No unique index or unique constraint ending with _PK.");
        }
        return TableCatalogEntry.ineligible(table, "Multiple unique indexes or unique constraints ending with _PK.");
    }
}
