package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.model.AuditTrailComparisonResult;
import com.danhaywood.cfct.model.ExecutionTimingResult;
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
    private final AutomationAuditTrailComparisonService auditTrailComparisonService;
    private final AutomationExecutionTimingService executionTimingService;
    private final Clock clock;
    private final AtomicBoolean refreshInProgress = new AtomicBoolean();

    @Autowired
    public AutomationComparisonService(
            final WebappComparisonProperties comparisonProperties,
            final WebappDatasourceProperties datasourceProperties,
            final WebappComparisonExecutionService comparisonExecutionService,
            final SqlServerCommandCatalogService commandCatalogService,
            final SqlServerTableCatalogService tableCatalogService,
            final CommandDrivenTableSelectionService commandDrivenTableSelectionService,
            final AutomationAuditTrailComparisonService auditTrailComparisonService,
            final AutomationExecutionTimingService executionTimingService) {
        this(
                comparisonProperties,
                datasourceProperties,
                comparisonExecutionService,
                commandCatalogService,
                tableCatalogService,
                commandDrivenTableSelectionService,
                auditTrailComparisonService,
                executionTimingService,
                Clock.systemUTC());
    }

    AutomationComparisonService(
            final WebappComparisonProperties comparisonProperties,
            final WebappDatasourceProperties datasourceProperties,
            final WebappComparisonExecutionService comparisonExecutionService,
            final SqlServerCommandCatalogService commandCatalogService,
            final SqlServerTableCatalogService tableCatalogService,
            final CommandDrivenTableSelectionService commandDrivenTableSelectionService,
            final AutomationAuditTrailComparisonService auditTrailComparisonService,
            final AutomationExecutionTimingService executionTimingService,
            final Clock clock) {
        this.comparisonProperties = comparisonProperties;
        this.datasourceProperties = datasourceProperties;
        this.comparisonExecutionService = comparisonExecutionService;
        this.commandCatalogService = commandCatalogService;
        this.tableCatalogService = tableCatalogService;
        this.commandDrivenTableSelectionService = commandDrivenTableSelectionService;
        this.auditTrailComparisonService = auditTrailComparisonService;
        this.executionTimingService = executionTimingService;
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
                        withAutomationMetadata(
                                EMPTY_COMPARISON_JSON,
                                null,
                                backgroundCommands,
                                null,
                                null,
                                "no_completed_foreground"),
                        Instant.now(clock),
                        0,
                        null,
                        backgroundCommands));
            }

            final List<CommandCatalogEntry> leftBackground = backgroundDescendants(leftCatalog, command.interactionId());
            final List<CommandCatalogEntry> rightBackground = backgroundDescendants(rightCatalog, command.interactionId());
            final CommandMetadata commandMetadata = CommandMetadata.from(command);
            final ResolvedTableFootprint tableFootprint = dynamicallyResolvedTables(
                    context,
                    command,
                    leftBackground,
                    rightBackground);
            final BackgroundCommandsMetadata backgroundCommands = BackgroundCommandsMetadata
                    .from(leftBackground, rightBackground)
                    .withTableFootprint(tableFootprint.backgroundMetadata());
            final AuditTrailComparisonResult auditTrailComparison = auditTrailComparisonService.compare(
                    context,
                    command.interactionId(),
                    completedBackgroundInteractionIds(leftBackground),
                    completedBackgroundInteractionIds(rightBackground));
            final ExecutionTimingResult executionTiming = executionTimingService.read(
                    context,
                    command.interactionId(),
                    terminalBackgroundInteractionIds(leftBackground),
                    terminalBackgroundInteractionIds(rightBackground));
            if (tableFootprint.comparisonTables().isEmpty()) {
                return AutomationRefreshResult.success(new LatestAutomationResult(
                        withAutomationMetadata(
                                EMPTY_COMPARISON_JSON,
                                commandMetadata,
                                backgroundCommands,
                                auditTrailComparison,
                                executionTiming,
                                null),
                        Instant.now(clock),
                        0,
                        commandMetadata,
                        backgroundCommands));
            }

            final MultiTableComparisonRequest request = MultiTableComparisonRequest.forTables(
                    List.copyOf(tableFootprint.comparisonTables()));
            final WebappComparisonExecutionService.ComparisonExecutionOutcome outcome = comparisonExecutionService.compare(
                    request,
                    null,
                    context);
            final LatestAutomationResult result = new LatestAutomationResult(
                    withAutomationMetadata(
                            outcome.json(),
                            commandMetadata,
                            backgroundCommands,
                            auditTrailComparison,
                            executionTiming,
                            null),
                    Instant.now(clock),
                    request.tables().size(),
                    commandMetadata,
                    backgroundCommands);
            return AutomationRefreshResult.success(result);
        } finally {
            refreshInProgress.set(false);
        }
    }

    private ResolvedTableFootprint dynamicallyResolvedTables(
            final AuthenticatedConnectionContext context,
            final CommandCatalogEntry command,
            final List<CommandCatalogEntry> leftBackground,
            final List<CommandCatalogEntry> rightBackground) {
        final List<TableCatalogEntry> leftTables = tableCatalogService.discoverTableCatalog(context, DatabaseSide.LEFT);
        final List<TableCatalogEntry> rightTables = tableCatalogService.discoverTableCatalog(context, DatabaseSide.RIGHT);
        final Set<TableRef> leftForegroundTables = commandDrivenTableSelectionService.resolveTouchedBusinessTables(
                List.of(command.interactionId()),
                leftTables,
                context,
                DatabaseSide.LEFT);
        final Set<TableRef> rightForegroundTables = commandDrivenTableSelectionService.resolveTouchedBusinessTables(
                List.of(command.interactionId()),
                rightTables,
                context,
                DatabaseSide.RIGHT);
        final Set<TableRef> leftBackgroundTables = commandDrivenTableSelectionService.resolveTouchedBusinessTables(
                completedBackgroundInteractionIds(leftBackground),
                leftTables,
                context,
                DatabaseSide.LEFT);
        final Set<TableRef> rightBackgroundTables = commandDrivenTableSelectionService.resolveTouchedBusinessTables(
                completedBackgroundInteractionIds(rightBackground),
                rightTables,
                context,
                DatabaseSide.RIGHT);
        final Set<TableRef> comparisonTables = new LinkedHashSet<>();
        comparisonTables.addAll(leftForegroundTables);
        comparisonTables.addAll(rightForegroundTables);
        comparisonTables.addAll(leftBackgroundTables);
        comparisonTables.addAll(rightBackgroundTables);
        return new ResolvedTableFootprint(comparisonTables, leftBackgroundTables, rightBackgroundTables);
    }

    private static List<String> completedBackgroundInteractionIds(
            final List<CommandCatalogEntry> backgroundCommands) {
        return backgroundCommands.stream()
                .filter(command -> statusOf(command) == BackgroundCommandStatus.COMPLETED)
                .map(CommandCatalogEntry::interactionId)
                .toList();
    }

    private static List<String> terminalBackgroundInteractionIds(
            final List<CommandCatalogEntry> backgroundCommands) {
        return backgroundCommands.stream()
                .filter(command -> statusOf(command) != BackgroundCommandStatus.PENDING)
                .map(CommandCatalogEntry::interactionId)
                .toList();
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
            final AuditTrailComparisonResult auditTrailComparison,
            final ExecutionTimingResult executionTiming,
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
            if (auditTrailComparison != null) {
                root.set("auditTrailComparison", JSON_MAPPER.valueToTree(auditTrailComparison));
            }
            if (executionTiming != null) {
                root.set("executionTiming", JSON_MAPPER.valueToTree(executionTiming));
            }
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

    private record ResolvedTableFootprint(
            Set<TableRef> comparisonTables,
            Set<TableRef> appABackgroundTables,
            Set<TableRef> appBBackgroundTables) {
        BackgroundTableFootprintMetadata backgroundMetadata() {
            return BackgroundTableFootprintMetadata.from(appABackgroundTables, appBBackgroundTables);
        }
    }

    public record TableIdentityMetadata(String schema, String name) {
        static TableIdentityMetadata from(final TableRef table) {
            return new TableIdentityMetadata(table.schemaName(), table.tableName());
        }
    }

    public record BackgroundTableFootprintMetadata(
            List<TableIdentityMetadata> appA,
            List<TableIdentityMetadata> appB,
            List<TableIdentityMetadata> union) {
        static BackgroundTableFootprintMetadata from(
                final Set<TableRef> appATables,
                final Set<TableRef> appBTables) {
            final Set<TableRef> union = new LinkedHashSet<>(appATables);
            union.addAll(appBTables);
            return new BackgroundTableFootprintMetadata(
                    sortedTables(appATables),
                    sortedTables(appBTables),
                    sortedTables(union));
        }

        static BackgroundTableFootprintMetadata empty() {
            return new BackgroundTableFootprintMetadata(List.of(), List.of(), List.of());
        }

        private static List<TableIdentityMetadata> sortedTables(final Set<TableRef> tables) {
            return tables.stream()
                    .sorted(Comparator.comparing(TableRef::displayName, String.CASE_INSENSITIVE_ORDER))
                    .map(TableIdentityMetadata::from)
                    .toList();
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
            BackgroundSideMetadata appB,
            BackgroundTableFootprintMetadata tableFootprint) {
        public BackgroundCommandsMetadata(final int pending) {
            this(
                    pending,
                    0,
                    0,
                    BackgroundSideMetadata.empty(),
                    BackgroundSideMetadata.empty(),
                    BackgroundTableFootprintMetadata.empty());
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
                    right,
                    BackgroundTableFootprintMetadata.empty());
        }

        BackgroundCommandsMetadata withTableFootprint(final BackgroundTableFootprintMetadata tableFootprint) {
            return new BackgroundCommandsMetadata(pending, completed, failed, appA, appB, tableFootprint);
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
