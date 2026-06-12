package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
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
        return discoverCommandCatalog(authenticatedContextHolder.required());
    }

    public List<CommandCatalogEntry> discoverCommandCatalog(final AuthenticatedConnectionContext authenticatedContext) {
        final WebappDataSources dataSources = dataSourceConfiguration.dataSourcesFor(authenticatedContext);
        final String sql = """
                SELECT
                    CONVERT(varchar(36), interactionId) AS interaction_id,
                    logicalMemberIdentifier,
                    target,
                    replayState,
                    executeIn,
                    CONVERT(varchar(33), [timestamp], 126) AS timestamp_text,
                    CONVERT(varchar(33), completedAt, 126) AS completed_at_text
                FROM causewayExtCommandLog.CommandLogEntry
                ORDER BY [timestamp] DESC, interactionId DESC
                """;

        try (Connection jdbc = dataSources.left().getConnection();
             PreparedStatement statement = jdbc.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            final List<CommandCatalogEntry> rows = new ArrayList<>();
            while (resultSet.next()) {
                rows.add(mapDiscoveredCommand(
                        resultSet.getString("interaction_id"),
                        resultSet.getString("logicalMemberIdentifier"),
                        resultSet.getString("target"),
                        resultSet.getString("replayState"),
                        resultSet.getString("executeIn"),
                        resultSet.getString("timestamp_text"),
                        resultSet.getString("completed_at_text")));
            }
            return rows;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to discover command catalog for selection.", ex);
        }
    }

    static CommandCatalogEntry mapDiscoveredCommand(
            final String interactionId,
            final String logicalMemberIdentifier,
            final String target,
            final String replayState,
            final String executeIn,
            final String timestamp,
            final String completedAt) {
        return new CommandCatalogEntry(
                interactionId,
                logicalMemberIdentifier,
                target,
                replayState,
                executeIn,
                timestamp,
                completedAt,
                false);
    }
}
