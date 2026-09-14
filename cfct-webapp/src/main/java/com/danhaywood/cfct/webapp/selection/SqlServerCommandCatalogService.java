package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContextHolder;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.cfct.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SqlServerCommandCatalogService {

    private final WebappDataSourceConfiguration dataSourceConfiguration;
    private final AuthenticatedConnectionContextHolder authenticatedContextHolder;

    public SqlServerCommandCatalogService(
            final WebappDataSourceConfiguration dataSourceConfiguration,
            final AuthenticatedConnectionContextHolder authenticatedContextHolder) {
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.authenticatedContextHolder = authenticatedContextHolder;
    }

    public List<CommandCatalogEntry> discoverCommandCatalog() {
        return discoverCommandCatalog(authenticatedContextHolder.required(), DatabaseSide.LEFT);
    }

    public List<CommandCatalogEntry> discoverCommandCatalog(final AuthenticatedConnectionContext authenticatedContext) {
        return discoverCommandCatalog(authenticatedContext, DatabaseSide.LEFT);
    }

    public List<CommandCatalogEntry> discoverCommandCatalog(
            final AuthenticatedConnectionContext authenticatedContext,
            final DatabaseSide side) {
        final WebappDataSources dataSources = dataSourceConfiguration.dataSourcesFor(authenticatedContext);
        final DataSource dataSource = side == DatabaseSide.LEFT ? dataSources.left() : dataSources.right();
        final String sql = """
                SELECT
                    CONVERT(varchar(36), interactionId) AS interaction_id,
                    CONVERT(varchar(36), parentInteractionId) AS parent_interaction_id,
                    logicalMemberIdentifier,
                    target,
                    replayState,
                    executeIn,
                    CONVERT(varchar(33), [timestamp], 126) AS timestamp_text,
                    CONVERT(varchar(33), completedAt, 126) AS completed_at_text
                FROM causewayExtCommandLog.CommandLogEntry
                ORDER BY [timestamp] DESC, interactionId DESC
                """;

        try (Connection jdbc = dataSource.getConnection();
             PreparedStatement statement = jdbc.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            final List<CommandCatalogEntry> rows = new ArrayList<>();
            while (resultSet.next()) {
                rows.add(mapDiscoveredCommand(
                        resultSet.getString("interaction_id"),
                        resultSet.getString("parent_interaction_id"),
                        resultSet.getString("logicalMemberIdentifier"),
                        resultSet.getString("target"),
                        resultSet.getString("replayState"),
                        resultSet.getString("executeIn"),
                        resultSet.getString("timestamp_text"),
                        resultSet.getString("completed_at_text")));
            }
            return rows;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to discover command catalog for " + side.name().toLowerCase() + " database.", ex);
        }
    }

    static CommandCatalogEntry mapDiscoveredCommand(
            final String interactionId,
            final String parentInteractionId,
            final String logicalMemberIdentifier,
            final String target,
            final String replayState,
            final String executeIn,
            final String timestamp,
            final String completedAt) {
        return new CommandCatalogEntry(
                interactionId,
                parentInteractionId,
                logicalMemberIdentifier,
                target,
                replayState,
                executeIn,
                timestamp,
                completedAt,
                false);
    }
}
