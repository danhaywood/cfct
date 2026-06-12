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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class AutomationComparisonService {

    private static final String EMPTY_COMPARISON_JSON = """
            {
              \"hasDifferences\" : false,
              \"differingTables\" : [ ],
              \"comparedTables\" : [ ]
            }
            """;
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final WebappComparisonProperties comparisonProperties;
    private final WebappDatasourceProperties datasourceProperties;
    private final WebappComparisonExecutionService comparisonExecutionService;
    private final SqlServerCommandCatalogService commandCatalogService;
    private final SqlServerTableCatalogService tableCatalogService;
    private final CommandDrivenTableSelectionService commandDrivenTableSelectionService;
    private final Clock clock;
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
            final List<CommandCatalogEntry> commandCatalog = commandCatalogService.discoverCommandCatalog(context);
            final CommandCatalogEntry command = newestSuccessfulCommand(commandCatalog);
            if (command == null) {
                throw new IllegalStateException("No successful command is available for automation refresh.");
            }
            final CommandMetadata commandMetadata = CommandMetadata.from(command);
            final BackgroundCommandsMetadata backgroundCommandsMetadata = BackgroundCommandsMetadata.from(commandCatalog);
            final List<TableRef> tables = dynamicallyResolvedTables(context, command);
            if (tables.isEmpty()) {
                return AutomationRefreshResult.success(new LatestAutomationResult(
                        withAutomationMetadata(EMPTY_COMPARISON_JSON, commandMetadata, backgroundCommandsMetadata),
                        Instant.now(clock),
                        0,
                        commandMetadata,
                        backgroundCommandsMetadata));
            }
            final MultiTableComparisonRequest request = MultiTableComparisonRequest.forTables(tables);
            final WebappComparisonExecutionService.ComparisonExecutionOutcome outcome = comparisonExecutionService.compare(
                    request,
                    null,
                    context);
            final LatestAutomationResult result = new LatestAutomationResult(
                    withAutomationMetadata(outcome.json(), commandMetadata, backgroundCommandsMetadata),
                    Instant.now(clock),
                    request.tables().size(),
                    commandMetadata,
                    backgroundCommandsMetadata);
            return AutomationRefreshResult.success(result);
        } finally {
            refreshInProgress.set(false);
        }
    }

    private List<TableRef> dynamicallyResolvedTables(
            final AuthenticatedConnectionContext context,
            final CommandCatalogEntry command) {
        final List<TableCatalogEntry> tableCatalog = tableCatalogService.discoverTableCatalog(context);
        final Set<TableRef> touchedTables = commandDrivenTableSelectionService.resolveTouchedBusinessTables(
                List.of(command.interactionId()),
                tableCatalog,
                context);
        return List.copyOf(touchedTables);
    }

    private CommandCatalogEntry newestSuccessfulCommand(final List<CommandCatalogEntry> entries) {
        return entries.stream()
                .filter(entry -> "OK".equalsIgnoreCase(entry.replayState()))
                .max(Comparator.comparing(CommandCatalogEntry::timestamp, String.CASE_INSENSITIVE_ORDER))
                .orElse(null);
    }

    private static String withAutomationMetadata(
            final String json,
            final CommandMetadata commandMetadata,
            final BackgroundCommandsMetadata backgroundCommandsMetadata) {
        try {
            final ObjectNode root = (ObjectNode) JSON_MAPPER.readTree(json);
            final ObjectNode commandNode = JSON_MAPPER.createObjectNode();
            commandNode.put("interactionId", commandMetadata.interactionId());
            commandNode.put("timestamp", commandMetadata.timestamp());
            root.set("command", commandNode);
            final ObjectNode backgroundCommandsNode = JSON_MAPPER.createObjectNode();
            backgroundCommandsNode.put("pending", backgroundCommandsMetadata.pending());
            root.set("backgroundCommands", backgroundCommandsNode);
            return JSON_MAPPER.writeValueAsString(root) + System.lineSeparator();
        } catch (JsonProcessingException | ClassCastException ex) {
            throw new IllegalStateException("Failed to add automation metadata to JSON comparison result", ex);
        }
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

    public record CommandMetadata(String interactionId, String timestamp) {
        static CommandMetadata from(final CommandCatalogEntry command) {
            return new CommandMetadata(command.interactionId(), command.timestamp());
        }
    }

    public record BackgroundCommandsMetadata(int pending) {
        static BackgroundCommandsMetadata from(final List<CommandCatalogEntry> commandCatalog) {
            return new BackgroundCommandsMetadata((int) commandCatalog.stream()
                    .filter(BackgroundCommandsMetadata::isPendingBackgroundCommand)
                    .count());
        }

        private static boolean isPendingBackgroundCommand(final CommandCatalogEntry command) {
            return "BACKGROUND".equalsIgnoreCase(command.executeIn())
                    && "PENDING".equalsIgnoreCase(command.replayState());
        }
    }

    public record LatestAutomationResult(
            String json,
            Instant completedAt,
            int tableCount,
            CommandMetadata command,
            BackgroundCommandsMetadata backgroundCommands) {
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
