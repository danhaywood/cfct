package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.exception.ComparisonException;
import com.danhaywood.cfct.model.AuditTrailEntryDescriptor;
import com.danhaywood.cfct.spi.AuditTrailEntryReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class AuditTrailEntryReaderSqlServer implements AuditTrailEntryReader {

    @Override
    public List<AuditTrailEntryDescriptor> read(
            final Connection connection,
            final Collection<String> interactionIds) {
        if (interactionIds == null || interactionIds.isEmpty()) {
            return List.of();
        }

        final List<String> ids = new ArrayList<>(interactionIds);
        final String placeholders = ids.stream().map(ignored -> "?").collect(Collectors.joining(", "));
        final String sql = """
                SELECT interactionId, sequence, target, propertyId AS memberIdentifier
                FROM causewayExtAuditTrail.AuditTrailEntry
                WHERE interactionId IN (%s)
                ORDER BY interactionId, sequence, target, propertyId
                """.formatted(placeholders);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                statement.setString(i + 1, ids.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                final List<AuditTrailEntryDescriptor> entries = new ArrayList<>();
                while (resultSet.next()) {
                    entries.add(new AuditTrailEntryDescriptor(
                            resultSet.getString("interactionId"),
                            resultSet.getInt("sequence"),
                            resultSet.getString("target"),
                            resultSet.getString("memberIdentifier")));
                }
                return List.copyOf(entries);
            }
        } catch (SQLException ex) {
            throw new ComparisonException("Failed to read audit trail entries", ex);
        }
    }
}
