package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.model.BackgroundExecutionTiming;
import com.danhaywood.cfct.model.CommandExecutionTimingObservation;
import com.danhaywood.cfct.model.ExecutionTimingResult;
import com.danhaywood.cfct.model.ExecutionTimingScope;
import com.danhaywood.cfct.model.ExecutionTimingSide;
import com.danhaywood.cfct.model.ForegroundExecutionTiming;
import com.danhaywood.cfct.spi.CommandExecutionTimingReader;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.cfct.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Service
public class AutomationExecutionTimingService {

    private final WebappDataSourceConfiguration dataSourceConfiguration;
    private final CommandExecutionTimingReader timingReader;

    public AutomationExecutionTimingService(
            final WebappDataSourceConfiguration dataSourceConfiguration,
            final CommandExecutionTimingReader timingReader) {
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.timingReader = timingReader;
    }

    public ExecutionTimingResult read(
            final AuthenticatedConnectionContext context,
            final String foregroundInteractionId,
            final Collection<String> appABackgroundInteractionIds,
            final Collection<String> appBBackgroundInteractionIds) {
        final WebappDataSources dataSources = dataSourceConfiguration.dataSourcesFor(context);
        try (Connection appAConnection = dataSources.left().getConnection();
             Connection appBConnection = dataSources.right().getConnection()) {
            final CommandExecutionTimingObservation appAForeground = firstOrNull(timingReader.read(
                    appAConnection,
                    List.of(foregroundInteractionId),
                    ExecutionTimingSide.APP_A,
                    ExecutionTimingScope.FOREGROUND));
            final CommandExecutionTimingObservation appBForeground = firstOrNull(timingReader.read(
                    appBConnection,
                    List.of(foregroundInteractionId),
                    ExecutionTimingSide.APP_B,
                    ExecutionTimingScope.FOREGROUND));
            final List<CommandExecutionTimingObservation> appABackground = timingReader.read(
                    appAConnection,
                    appABackgroundInteractionIds,
                    ExecutionTimingSide.APP_A,
                    ExecutionTimingScope.BACKGROUND);
            final List<CommandExecutionTimingObservation> appBBackground = timingReader.read(
                    appBConnection,
                    appBBackgroundInteractionIds,
                    ExecutionTimingSide.APP_B,
                    ExecutionTimingScope.BACKGROUND);
            return new ExecutionTimingResult(
                    new ForegroundExecutionTiming(appAForeground, appBForeground),
                    new BackgroundExecutionTiming(appABackground, appBBackground));
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to read automation execution timing.", ex);
        }
    }

    private static CommandExecutionTimingObservation firstOrNull(
            final List<CommandExecutionTimingObservation> observations) {
        return observations == null || observations.isEmpty() ? null : observations.getFirst();
    }
}
