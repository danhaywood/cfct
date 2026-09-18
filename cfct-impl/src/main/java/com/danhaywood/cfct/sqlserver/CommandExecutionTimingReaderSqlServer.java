package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.exception.ComparisonException;
import com.danhaywood.cfct.model.CommandExecutionTimingObservation;
import com.danhaywood.cfct.model.ExecutionTimingScope;
import com.danhaywood.cfct.model.ExecutionTimingSide;
import com.danhaywood.cfct.spi.CommandExecutionTimingReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CommandExecutionTimingReaderSqlServer implements CommandExecutionTimingReader {

    @Override
    public List<CommandExecutionTimingObservation> read(
            final Connection connection,
            final Collection<String> interactionIds,
            final ExecutionTimingSide side,
            final ExecutionTimingScope scope) {
        if (interactionIds == null || interactionIds.isEmpty()) {
            return List.of();
        }
        Objects.requireNonNull(side, "side");
        Objects.requireNonNull(scope, "scope");

        final List<String> ids = new ArrayList<>(interactionIds);
        final String placeholders = ids.stream().map(ignored -> "?").collect(Collectors.joining(", "));
        final String sql = """
                SELECT
                    CONVERT(varchar(36), interactionId) AS interaction_id,
                    CONVERT(varchar(36), parentInteractionId) AS parent_interaction_id,
                    logicalMemberIdentifier,
                    replayState,
                    startedAt,
                    completedAt
                FROM causewayExtCommandLog.CommandLogEntry
                WHERE interactionId IN (%s)
                ORDER BY logicalMemberIdentifier, startedAt, interactionId
                """.formatted(placeholders);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                statement.setString(i + 1, ids.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                final List<CommandExecutionTimingObservation> observations = new ArrayList<>();
                while (resultSet.next()) {
                    final LocalDateTime startedAt = localDateTime(resultSet.getTimestamp("startedAt"));
                    final LocalDateTime completedAt = localDateTime(resultSet.getTimestamp("completedAt"));
                    observations.add(new CommandExecutionTimingObservation(
                            side,
                            scope,
                            resultSet.getString("interaction_id"),
                            resultSet.getString("parent_interaction_id"),
                            resultSet.getString("logicalMemberIdentifier"),
                            resultSet.getString("replayState"),
                            isoText(startedAt),
                            isoText(completedAt),
                            durationMillis(startedAt, completedAt)));
                }
                return observations.stream()
                        .sorted(CommandExecutionTimingObservation.deterministicOrder())
                        .toList();
            }
        } catch (SQLException ex) {
            throw new ComparisonException("Failed to read command execution timing", ex);
        }
    }

    static Long durationMillis(final LocalDateTime startedAt, final LocalDateTime completedAt) {
        if (startedAt == null || completedAt == null || completedAt.isBefore(startedAt)) {
            return null;
        }
        return Duration.between(startedAt, completedAt).toMillis();
    }

    private static LocalDateTime localDateTime(final Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private static String isoText(final LocalDateTime value) {
        return value == null ? null : value.toString();
    }
}
