package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.exception.ComparisonException;
import com.danhaywood.cfct.spi.CommandAuditTouchedTableResolver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class CommandAuditTouchedTableResolverSqlServer implements CommandAuditTouchedTableResolver {

    @Override
    public SortedSet<String> resolveTouchedQualifiedTableNames(
            final Connection connection,
            final Collection<String> interactionIds) {
        if (interactionIds == null || interactionIds.isEmpty()) {
            return new TreeSet<>();
        }

        final LinkedHashSet<String> logicalTypeNames = readAuditTargetLogicalTypeNames(connection, interactionIds);
        if (logicalTypeNames.isEmpty()) {
            return new TreeSet<>();
        }

        return readQualifiedTableNames(connection, logicalTypeNames);
    }

    static Optional<String> parseLogicalTypeName(final String auditTarget) {
        if (auditTarget == null) {
            return Optional.empty();
        }
        final int separatorIndex = auditTarget.indexOf(':');
        if (separatorIndex <= 0 || separatorIndex >= auditTarget.length() - 1) {
            return Optional.empty();
        }
        return Optional.of(auditTarget.substring(0, separatorIndex));
    }

    private LinkedHashSet<String> readAuditTargetLogicalTypeNames(
            final Connection connection,
            final Collection<String> interactionIds) {
        final List<String> ids = new ArrayList<>(interactionIds);
        final String placeholders = ids.stream().map(ignored -> "?").collect(Collectors.joining(", "));
        final String sql = """
                SELECT DISTINCT a.target
                FROM causewayExtCommandLog.CommandLogEntry c
                JOIN causewayExtAuditTrail.AuditTrailEntry a ON c.interactionId = a.interactionId
                WHERE c.interactionId IN (%s)
                """.formatted(placeholders);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                statement.setString(i + 1, ids.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                final LinkedHashSet<String> logicalTypeNames = new LinkedHashSet<>();
                while (resultSet.next()) {
                    parseLogicalTypeName(resultSet.getString("target"))
                            .ifPresent(logicalTypeNames::add);
                }
                return logicalTypeNames;
            }
        } catch (SQLException ex) {
            throw new ComparisonException("Failed to resolve logical type names from audit targets", ex);
        }
    }

    private SortedSet<String> readQualifiedTableNames(
            final Connection connection,
            final Collection<String> logicalTypeNames) {
        final List<String> logicalTypes = new ArrayList<>(logicalTypeNames);
        final String placeholders = logicalTypes.stream().map(ignored -> "?").collect(Collectors.joining(", "));
        final String sql = """
                SELECT DISTINCT qualifiedName
                FROM util.LogicalTypeTableMapping
                WHERE logicalTypeName IN (%s)
                ORDER BY qualifiedName
                """.formatted(placeholders);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < logicalTypes.size(); i++) {
                statement.setString(i + 1, logicalTypes.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                final SortedSet<String> qualifiedTableNames = new TreeSet<>();
                while (resultSet.next()) {
                    qualifiedTableNames.add(resultSet.getString("qualifiedName"));
                }
                return qualifiedTableNames;
            }
        } catch (SQLException ex) {
            throw new ComparisonException("Failed to resolve qualified table names from logical type mappings", ex);
        }
    }
}
