package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.comparison.WebappComparisonExecutionService;
import com.danhaywood.cfct.webapp.config.WebappComparisonProperties;
import com.danhaywood.cfct.webapp.config.WebappDatasourceProperties;
import com.danhaywood.cfct.webapp.selection.CommandCatalogEntry;
import com.danhaywood.cfct.webapp.selection.CommandDrivenTableSelectionService;
import com.danhaywood.cfct.webapp.selection.DatabaseSide;
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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
            final List<CommandCatalogEntry> leftCatalog = commandCatalogService.discoverCommandCatalog(context, DatabaseSide.LEFT);
            final List<CommandCatalogEntry> rightCatalog = commandCatalogService.discoverCommandCatalog(context, DatabaseSide.RIGHT);
            final CommandCatalogEntry command = newestCommonSuccessfulForeground(leftCatalog, rightCatalog);
            if (command == null) {
                final BackgroundCommandsMetadata backgroundCommands = BackgroundCommandsMetadata.empty();
                return AutomationRefreshResult.success(new LatestAutomationResult(
                        withAutomationMetadata(EMPTY_COMPARISON_JSON, null, backgroundCommands, "no_completed_foreground"),
                        Instant.now(clock),
                        0,
                        null,
                        backgroundCommands));
            }

            final List<CommandCatalogEntry> leftBackground = backgroundDescendants(leftCatalog, command.interactionId());
            final List<CommandCatalogEntry> rightBackground = backgroundDescendants(rightCatalog, command.interactionId());
            final BackgroundCommandsMetadata backgroundCommands = BackgroundCommandsMetadata.from(leftBackground, rightBackground);
            final CommandMetadata commandMetadata = CommandMetadata.from(command);
            final Set<TableRef> tables = dynamicallyResolvedTables(
                    context,
                    command,
                    leftBackground,
                    rightBackground);
            if (tables.isEmpty()) {
                return AutomationRefreshResult.success(new LatestAutomationResult(
                        withAutomationMetadata(EMPTY_COMPARISON_JSON, commandMetadata, backgroundCommands, null),
                        Instant.now(clock),
                        0,
                        commandMetadata,
                        backgroundCommands));
            }

            final MultiTableComparisonRequest request = MultiTableComparisonRequest.forTables(List.copyOf(tables));
            final WebappComparisonExecutionService.ComparisonExecutionOutcome outcome = comparisonExecutionService.compare(
                    request,
                    null,
                    context);
            final LatestAutomationResult result = new LatestAutomationResult(
                    withAutomationMetadata(outcome.json(), commandMetadata, backgroundCommands, null),
                    Instant.now(clock),
                    request.tables().size(),
                    commandMetadata,
                    backgroundCommands);
            return AutomationRefreshResult.success(result);
        } finally {
            refreshInProgress.set(false);
        }
    }

    private Set<TableRef> dynamicallyResolvedTables(
            final AuthenticatedConnectionContext context,
            final CommandCatalogEntry command,
            final List<CommandCatalogEntry> leftBackground,
            final List<CommandCatalogEntry> rightBackground) {
        final List<TableCatalogEntry> leftTables = tableCatalogService.discoverTableCatalog(context, DatabaseSide.LEFT);
        final List<TableCatalogEntry> rightTables = tableCatalogService.discoverTableCatalog(context, DatabaseSide.RIGHT);
        final Set<TableRef> resolved = new LinkedHashSet<>();
        resolved.addAll(commandDrivenTableSelectionService.resolveTouchedBusinessTables(
                completedInteractionIds(command, leftBackground),
                leftTables,
                context,
                DatabaseSide.LEFT));
        resolved.addAll(commandDrivenTableSelectionService.resolveTouchedBusinessTables(
                completedInteractionIds(command, rightBackground),
                rightTables,
                context,
                DatabaseSide.RIGHT));
        return resolved;
    }

    private static List<String> completedInteractionIds(
            final CommandCatalogEntry root,
            final List<CommandCatalogEntry> backgroundCommands) {
        final List<String> interactionIds = new ArrayList<>();
        interactionIds.add(root.interactionId());
        backgroundCommands.stream()
                .filter(command -> statusOf(command) == BackgroundCommandStatus.COMPLETED)
                .map(CommandCatalogEntry::interactionId)
                .forEach(interactionIds::add);
        return interactionIds;
    }

    private static CommandCatalogEntry newestCommonSuccessfulForeground(
            final List<CommandCatalogEntry> leftCatalog,
            final List<CommandCatalogEntry> rightCatalog) {
        final Set<String> successfulRightForegroundIds = new HashSet<>();
        rightCatalog.stream()
                .filter(AutomationComparisonService::isSuccessfulForeground)
                .map(CommandCatalogEntry::interactionId)
                .map(AutomationComparisonService::normalizedId)
                .forEach(successfulRightForegroundIds::add);
        return leftCatalog.stream()
                .filter(AutomationComparisonService::isSuccessfulForeground)
                .filter(command -> successfulRightForegroundIds.contains(normalizedId(command.interactionId())))
                .max(Comparator.comparing(CommandCatalogEntry::timestamp, String.CASE_INSENSITIVE_ORDER))
                .orElse(null);
    }

    private static boolean isSuccessfulForeground(final CommandCatalogEntry command) {
        return "FOREGROUND".equalsIgnoreCase(command.executeIn())
                && "OK".equalsIgnoreCase(command.replayState());
    }

    static List<CommandCatalogEntry> backgroundDescendants(
            final List<CommandCatalogEntry> catalog,
            final String rootInteractionId) {
        final List<CommandCatalogEntry> descendants = new ArrayList<>();
        final Set<String> discoveredParentIds = new HashSet<>();
        final ArrayDeque<String> pendingParentIds = new ArrayDeque<>();
        pendingParentIds.add(normalizedId(rootInteractionId));
        while (!pendingParentIds.isEmpty()) {
            final String parentId = pendingParentIds.removeFirst();
            if (!discoveredParentIds.add(parentId)) {
                continue;
            }
            catalog.stream()
                    .filter(command -> "BACKGROUND".equalsIgnoreCase(command.executeIn()))
                    .filter(command -> parentId.equals(normalizedId(command.parentInteractionId())))
                    .forEach(command -> {
                        descendants.add(command);
                        pendingParentIds.addLast(normalizedId(command.interactionId()));
                    });
        }
        descendants.sort(Comparator.comparing(CommandCatalogEntry::timestamp, String.CASE_INSENSITIVE_ORDER));
        return List.copyOf(descendants);
    }

    private static String normalizedId(final String interactionId) {
        return interactionId == null ? "" : interactionId.toLowerCase(Locale.ROOT);
    }

    private static BackgroundCommandStatus statusOf(final CommandCatalogEntry command) {
        if ("FAILED".equalsIgnoreCase(command.replayState())) {
            return BackgroundCommandStatus.FAILED;
        }
        if ("PENDING".equalsIgnoreCase(command.replayState()) || isBlank(command.completedAt())) {
            return BackgroundCommandStatus.PENDING;
        }
        return BackgroundCommandStatus.COMPLETED;
    }

    private static boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    private static String withAutomationMetadata(
            final String json,
            final CommandMetadata commandMetadata,
            final BackgroundCommandsMetadata backgroundCommandsMetadata,
            final String status) {
        try {
            final ObjectNode root = (ObjectNode) JSON_MAPPER.readTree(json);
            if (status != null) {
                root.put("status", status);
            }
            if (commandMetadata != null) {
                root.set("command", JSON_MAPPER.valueToTree(commandMetadata));
            }
            root.set("backgroundCommands", JSON_MAPPER.valueToTree(backgroundCommandsMetadata));
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

    public enum BackgroundCommandStatus {
        PENDING,
        COMPLETED,
        FAILED
    }

    public record BackgroundCommandMetadata(
            String interactionId,
            String parentInteractionId,
            String logicalMemberIdentifier,
            String replayState,
            String executeIn,
            String timestamp,
            String completedAt,
            BackgroundCommandStatus status) {
        static BackgroundCommandMetadata from(final CommandCatalogEntry command) {
            return new BackgroundCommandMetadata(
                    command.interactionId(),
                    command.parentInteractionId(),
                    command.logicalMemberIdentifier(),
                    command.replayState(),
                    command.executeIn(),
                    command.timestamp(),
                    command.completedAt(),
                    statusOf(command));
        }
    }

    public record BackgroundSideMetadata(
            int pending,
            int completed,
            int failed,
            List<BackgroundCommandMetadata> commands) {
        static BackgroundSideMetadata from(final List<CommandCatalogEntry> commands) {
            final List<BackgroundCommandMetadata> details = commands.stream()
                    .map(BackgroundCommandMetadata::from)
                    .toList();
            return new BackgroundSideMetadata(
                    (int) details.stream().filter(command -> command.status() == BackgroundCommandStatus.PENDING).count(),
                    (int) details.stream().filter(command -> command.status() == BackgroundCommandStatus.COMPLETED).count(),
                    (int) details.stream().filter(command -> command.status() == BackgroundCommandStatus.FAILED).count(),
                    details);
        }

        static BackgroundSideMetadata empty() {
            return new BackgroundSideMetadata(0, 0, 0, List.of());
        }
    }

    public record BackgroundCommandsMetadata(
            int pending,
            int completed,
            int failed,
            BackgroundSideMetadata appA,
            BackgroundSideMetadata appB) {
        public BackgroundCommandsMetadata(final int pending) {
            this(pending, 0, 0, BackgroundSideMetadata.empty(), BackgroundSideMetadata.empty());
        }

        static BackgroundCommandsMetadata from(
                final List<CommandCatalogEntry> leftCommands,
                final List<CommandCatalogEntry> rightCommands) {
            final BackgroundSideMetadata left = BackgroundSideMetadata.from(leftCommands);
            final BackgroundSideMetadata right = BackgroundSideMetadata.from(rightCommands);
            return new BackgroundCommandsMetadata(
                    left.pending() + right.pending(),
                    left.completed() + right.completed(),
                    left.failed() + right.failed(),
                    left,
                    right);
        }

        static BackgroundCommandsMetadata empty() {
            return new BackgroundCommandsMetadata(0);
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
