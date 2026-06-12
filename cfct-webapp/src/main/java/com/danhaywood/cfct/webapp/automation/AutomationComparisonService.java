package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.comparison.WebappComparisonExecutionService;
import com.danhaywood.cfct.webapp.config.WebappComparisonProperties;
import com.danhaywood.cfct.webapp.config.WebappDatasourceProperties;
import com.danhaywood.cfct.webapp.selection.CommandCatalogEntry;
import com.danhaywood.cfct.webapp.selection.CommandDrivenTableSelectionService;
import com.danhaywood.cfct.webapp.selection.SqlServerCommandCatalogService;
import com.danhaywood.cfct.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.cfct.webapp.selection.TableCatalogEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AutomationComparisonService {

    private final WebappComparisonProperties comparisonProperties;
    private final WebappDatasourceProperties datasourceProperties;
    private final WebappComparisonExecutionService comparisonExecutionService;
    private final SqlServerCommandCatalogService commandCatalogService;
    private final SqlServerTableCatalogService tableCatalogService;
    private final CommandDrivenTableSelectionService commandDrivenTableSelectionService;
    private final Clock clock;
    private final AtomicReference<LatestAutomationResult> latestResult = new AtomicReference<>();
    private final AtomicBoolean refreshInProgress = new AtomicBoolean();

    @Autowired
    public AutomationComparisonService(
            final WebappComparisonProperties comparisonProperties,
            final WebappDatasourceProperties datasourceProperties,
            final WebappComparisonExecutionService comparisonExecutionService,
            final SqlServerCommandCatalogService commandCatalogService,
            final SqlServerTableCatalogService tableCatalogService,
            final CommandDrivenTableSelectionService commandDrivenTableSelectionService) {
        this(
                comparisonProperties,
                datasourceProperties,
                comparisonExecutionService,
                commandCatalogService,
                tableCatalogService,
                commandDrivenTableSelectionService,
                Clock.systemUTC());
    }

    AutomationComparisonService(
            final WebappComparisonProperties comparisonProperties,
            final WebappDatasourceProperties datasourceProperties,
            final WebappComparisonExecutionService comparisonExecutionService,
            final SqlServerCommandCatalogService commandCatalogService,
            final SqlServerTableCatalogService tableCatalogService,
            final CommandDrivenTableSelectionService commandDrivenTableSelectionService,
            final Clock clock) {
        this.comparisonProperties = comparisonProperties;
        this.datasourceProperties = datasourceProperties;
        this.comparisonExecutionService = comparisonExecutionService;
        this.commandCatalogService = commandCatalogService;
        this.tableCatalogService = tableCatalogService;
        this.commandDrivenTableSelectionService = commandDrivenTableSelectionService;
        this.clock = clock;
    }

    public AutomationRefreshResult refresh() {
        ensureEnabled();
        if (!refreshInProgress.compareAndSet(false, true)) {
            return AutomationRefreshResult.inProgress();
        }
        try {
            final AuthenticatedConnectionContext context = automationConnectionContext();
            final List<TableRef> tables = dynamicallyResolvedTables(context);
            final MultiTableComparisonRequest request = MultiTableComparisonRequest.forTables(tables);
            final WebappComparisonExecutionService.ComparisonExecutionOutcome outcome = comparisonExecutionService.compare(
                    request,
                    null,
                    context);
            final LatestAutomationResult result = new LatestAutomationResult(
                    outcome.json(),
                    Instant.now(clock),
                    request.tables().size());
            latestResult.set(result);
            return AutomationRefreshResult.success(result);
        } finally {
            refreshInProgress.set(false);
        }
    }

    public LatestAutomationResult latestResult() {
        ensureEnabled();
        return latestResult.get();
    }

    private List<TableRef> dynamicallyResolvedTables(final AuthenticatedConnectionContext context) {
        final String interactionId = newestSuccessfulCommandInteractionId(commandCatalogService.discoverCommandCatalog(context));
        if (interactionId == null) {
            throw new IllegalStateException("No successful command is available for automation refresh.");
        }
        final List<TableCatalogEntry> tableCatalog = tableCatalogService.discoverTableCatalog(context);
        final Set<TableRef> touchedTables = commandDrivenTableSelectionService.resolveTouchedBusinessTables(
                List.of(interactionId),
                tableCatalog,
                context);
        if (touchedTables.isEmpty()) {
            throw new IllegalStateException("No eligible touched business tables were resolved for command " + interactionId + ".");
        }
        return List.copyOf(touchedTables);
    }

    private String newestSuccessfulCommandInteractionId(final List<CommandCatalogEntry> entries) {
        return entries.stream()
                .filter(entry -> "OK".equalsIgnoreCase(entry.replayState()))
                .max(Comparator.comparing(CommandCatalogEntry::timestamp, String.CASE_INSENSITIVE_ORDER))
                .map(CommandCatalogEntry::interactionId)
                .orElse(null);
    }

    private void ensureEnabled() {
        if (!comparisonProperties.getAutomation().isEnabled()) {
            throw new AutomationDisabledException();
        }
    }

    private AuthenticatedConnectionContext automationConnectionContext() {
        final WebappComparisonProperties.Automation automation = comparisonProperties.getAutomation();
        return new AuthenticatedConnectionContext(
                datasourceProperties.getUrl(),
                datasourceProperties.getDriverClassName(),
                datasourceProperties.getUsername(),
                datasourceProperties.getPassword(),
                configuredOrFallback(automation.getLeftDatabase(), comparisonProperties.getConnection().getLeftDatabase(), "left database"),
                configuredOrFallback(automation.getRightDatabase(), comparisonProperties.getConnection().getRightDatabase(), "right database"));
    }

    private static String configuredOrFallback(final String value, final String fallback, final String label) {
        if (value != null && !value.isBlank()) {
            return value;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        throw new IllegalStateException("Automation " + label + " must be configured.");
    }

    public record LatestAutomationResult(String json, Instant completedAt, int tableCount) {
        public String filename() {
            return "comparison-" + completedAt.toString().replace(':', '-') + ".json";
        }
    }

    public record AutomationRefreshResult(boolean conflict, LatestAutomationResult latestResult) {
        public static AutomationRefreshResult inProgress() {
            return new AutomationRefreshResult(true, null);
        }

        public static AutomationRefreshResult success(final LatestAutomationResult latestResult) {
            return new AutomationRefreshResult(false, latestResult);
        }
    }

    public static class AutomationDisabledException extends RuntimeException {
    }
}
