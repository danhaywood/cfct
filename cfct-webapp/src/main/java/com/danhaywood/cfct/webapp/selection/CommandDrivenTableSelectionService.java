package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.spi.CommandAuditTouchedTableResolver;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContextHolder;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.cfct.webapp.config.WebappDataSources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class CommandDrivenTableSelectionService {

    private final WebappDataSourceConfiguration dataSourceConfiguration;
    private final AuthenticatedConnectionContextHolder authenticatedContextHolder;
    private final CommandAuditTouchedTableResolver touchedTableResolver;

    public CommandDrivenTableSelectionService() {
        this.dataSourceConfiguration = null;
        this.authenticatedContextHolder = null;
        this.touchedTableResolver = null;
    }

    @Autowired
    public CommandDrivenTableSelectionService(
            final WebappDataSourceConfiguration dataSourceConfiguration,
            final AuthenticatedConnectionContextHolder authenticatedContextHolder,
            final CommandAuditTouchedTableResolver touchedTableResolver) {
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.authenticatedContextHolder = authenticatedContextHolder;
        this.touchedTableResolver = touchedTableResolver;
    }

    public Set<TableRef> resolveTouchedBusinessTables(
            final Collection<String> selectedInteractionIds,
            final List<TableCatalogEntry> visibleTableCatalog) {
        if (touchedTableResolver == null || dataSourceConfiguration == null || authenticatedContextHolder == null) {
            return Set.of();
        }
        if (selectedInteractionIds == null || selectedInteractionIds.isEmpty() || visibleTableCatalog == null || visibleTableCatalog.isEmpty()) {
            return Set.of();
        }

        final Set<TableRef> visibleEligibleTables = visibleEligibleTables(visibleTableCatalog);
        if (visibleEligibleTables.isEmpty()) {
            return Set.of();
        }

        final Set<TableRef> resolved = resolveTouchedTableRefs(selectedInteractionIds, authenticatedContextHolder.required());
        if (resolved.isEmpty()) {
            return Set.of();
        }

        final LinkedHashSet<TableRef> filtered = new LinkedHashSet<>();
        for (TableRef table : resolved) {
            if (visibleEligibleTables.contains(table)) {
                filtered.add(table);
            }
        }
        return filtered;
    }

    public Set<TableRef> resolveTouchedBusinessTables(
            final Collection<String> selectedInteractionIds,
            final List<TableCatalogEntry> visibleTableCatalog,
            final AuthenticatedConnectionContext authenticatedContext) {
        if (selectedInteractionIds == null || selectedInteractionIds.isEmpty() || visibleTableCatalog == null || visibleTableCatalog.isEmpty()) {
            return Set.of();
        }

        final Set<TableRef> visibleEligibleTables = visibleEligibleTables(visibleTableCatalog);
        if (visibleEligibleTables.isEmpty()) {
            return Set.of();
        }

        final Set<TableRef> resolved = resolveTouchedTableRefs(selectedInteractionIds, authenticatedContext);
        if (resolved.isEmpty()) {
            return Set.of();
        }

        final LinkedHashSet<TableRef> filtered = new LinkedHashSet<>();
        for (TableRef table : resolved) {
            if (visibleEligibleTables.contains(table)) {
                filtered.add(table);
            }
        }
        return filtered;
    }

    private Set<TableRef> resolveTouchedTableRefs(
            final Collection<String> selectedInteractionIds,
            final AuthenticatedConnectionContext authenticatedContext) {
        final WebappDataSources dataSources = dataSourceConfiguration.dataSourcesFor(authenticatedContext);
        try (Connection connection = dataSources.left().getConnection()) {
            final Set<String> qualifiedTableNames = touchedTableResolver.resolveTouchedQualifiedTableNames(connection, selectedInteractionIds);
            final LinkedHashSet<TableRef> parsed = new LinkedHashSet<>();
            for (String qualifiedName : qualifiedTableNames) {
                parseQualifiedName(qualifiedName).ifPresent(parsed::add);
            }
            return parsed;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to resolve command-driven touched tables.", ex);
        }
    }

    private Set<TableRef> visibleEligibleTables(final List<TableCatalogEntry> visibleTableCatalog) {
        final LinkedHashSet<TableRef> eligible = new LinkedHashSet<>();
        for (TableCatalogEntry entry : visibleTableCatalog) {
            if (entry.eligible()) {
                eligible.add(entry.table());
            }
        }
        return eligible;
    }

    static java.util.Optional<TableRef> parseQualifiedName(final String qualifiedName) {
        if (qualifiedName == null || qualifiedName.isBlank()) {
            return java.util.Optional.empty();
        }
        final String normalized = qualifiedName.trim();
        final int separatorIndex = normalized.lastIndexOf('.');
        if (separatorIndex <= 0 || separatorIndex >= normalized.length() - 1) {
            return java.util.Optional.empty();
        }
        final String schemaName = normalized.substring(0, separatorIndex).trim();
        final String tableName = normalized.substring(separatorIndex + 1).trim();
        if (schemaName.isBlank() || tableName.isBlank()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(new TableRef(schemaName, tableName));
    }
}
