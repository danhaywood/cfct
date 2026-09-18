package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.model.CommandExecutionTimingObservation;
import com.danhaywood.cfct.model.ExecutionTimingResult;
import com.danhaywood.cfct.model.ExecutionTimingScope;
import com.danhaywood.cfct.model.ExecutionTimingSide;
import com.danhaywood.cfct.spi.CommandExecutionTimingReader;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.cfct.webapp.config.WebappDataSources;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutomationExecutionTimingServiceTest {

    @Test
    void readsForegroundAndEachSidesIndependentTerminalBackgroundIds() throws Exception {
        final AuthenticatedConnectionContext context = new AuthenticatedConnectionContext(
                "jdbc", "driver", "user", "password", "left", "right");
        final WebappDataSourceConfiguration dataSourceConfiguration = mock(WebappDataSourceConfiguration.class);
        final DataSource leftDataSource = mock(DataSource.class);
        final DataSource rightDataSource = mock(DataSource.class);
        final Connection leftConnection = mock(Connection.class);
        final Connection rightConnection = mock(Connection.class);
        when(leftDataSource.getConnection()).thenReturn(leftConnection);
        when(rightDataSource.getConnection()).thenReturn(rightConnection);
        when(dataSourceConfiguration.dataSourcesFor(context)).thenReturn(
                new WebappDataSources(mock(DataSource.class), leftDataSource, rightDataSource));
        final CommandExecutionTimingReader reader = mock(CommandExecutionTimingReader.class);
        final CommandExecutionTimingObservation foregroundA = observation(
                ExecutionTimingSide.APP_A, ExecutionTimingScope.FOREGROUND, "root", null, "Type#act", 120L);
        final CommandExecutionTimingObservation foregroundB = observation(
                ExecutionTimingSide.APP_B, ExecutionTimingScope.FOREGROUND, "root", null, "Type#act", 150L);
        final CommandExecutionTimingObservation backgroundA = observation(
                ExecutionTimingSide.APP_A, ExecutionTimingScope.BACKGROUND, "left-child", "root", "Task#run", 20L);
        final CommandExecutionTimingObservation failedBackgroundA = observation(
                ExecutionTimingSide.APP_A, ExecutionTimingScope.BACKGROUND, "left-failed", "root", "Task#fail", 30L);
        final CommandExecutionTimingObservation backgroundB = observation(
                ExecutionTimingSide.APP_B, ExecutionTimingScope.BACKGROUND, "right-child", "root", "Task#run", 40L);
        when(reader.read(leftConnection, List.of("root"), ExecutionTimingSide.APP_A, ExecutionTimingScope.FOREGROUND))
                .thenReturn(List.of(foregroundA));
        when(reader.read(rightConnection, List.of("root"), ExecutionTimingSide.APP_B, ExecutionTimingScope.FOREGROUND))
                .thenReturn(List.of(foregroundB));
        when(reader.read(
                leftConnection,
                List.of("left-child", "left-failed"),
                ExecutionTimingSide.APP_A,
                ExecutionTimingScope.BACKGROUND)).thenReturn(List.of(backgroundA, failedBackgroundA));
        when(reader.read(
                rightConnection,
                List.of("right-child"),
                ExecutionTimingSide.APP_B,
                ExecutionTimingScope.BACKGROUND)).thenReturn(List.of(backgroundB));

        final ExecutionTimingResult result = new AutomationExecutionTimingService(
                dataSourceConfiguration,
                reader).read(
                        context,
                        "root",
                        List.of("left-child", "left-failed"),
                        List.of("right-child"));

        assertThat(result.foreground().appA()).isEqualTo(foregroundA);
        assertThat(result.foreground().appB()).isEqualTo(foregroundB);
        assertThat(result.background().appA()).containsExactly(failedBackgroundA, backgroundA);
        assertThat(result.background().appB()).containsExactly(backgroundB);
        verify(leftConnection).close();
        verify(rightConnection).close();
    }

    private static CommandExecutionTimingObservation observation(
            final ExecutionTimingSide side,
            final ExecutionTimingScope scope,
            final String interactionId,
            final String parentInteractionId,
            final String memberIdentifier,
            final Long durationMillis) {
        return new CommandExecutionTimingObservation(
                side,
                scope,
                interactionId,
                parentInteractionId,
                memberIdentifier,
                "OK",
                "2026-04-05T10:00:00",
                "2026-04-05T10:00:01",
                durationMillis);
    }
}
