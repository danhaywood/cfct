package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ExecutionTimingScope;
import com.danhaywood.cfct.model.ExecutionTimingSide;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class CommandExecutionTimingReaderSqlServerTest {

    @Test
    void emptyInteractionCollectionDoesNotAccessTheConnection() {
        final Connection connection = mock(Connection.class);

        assertThat(new CommandExecutionTimingReaderSqlServer().read(
                connection,
                List.of(),
                ExecutionTimingSide.APP_A,
                ExecutionTimingScope.FOREGROUND)).isEmpty();

        verifyNoInteractions(connection);
    }

    @Test
    void calculatesValidDurationInMilliseconds() {
        assertThat(CommandExecutionTimingReaderSqlServer.durationMillis(
                LocalDateTime.parse("2026-04-05T10:00:00.125"),
                LocalDateTime.parse("2026-04-05T10:00:02.375")))
                .isEqualTo(2_250L);
    }

    @Test
    void preservesUnavailableDurationForMissingTimestamp() {
        assertThat(CommandExecutionTimingReaderSqlServer.durationMillis(
                LocalDateTime.parse("2026-04-05T10:00:00"),
                null)).isNull();
        assertThat(CommandExecutionTimingReaderSqlServer.durationMillis(
                null,
                LocalDateTime.parse("2026-04-05T10:00:01"))).isNull();
    }

    @Test
    void preservesUnavailableDurationForInvalidInterval() {
        assertThat(CommandExecutionTimingReaderSqlServer.durationMillis(
                LocalDateTime.parse("2026-04-05T10:00:02"),
                LocalDateTime.parse("2026-04-05T10:00:01"))).isNull();
    }
}
