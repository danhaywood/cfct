package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.model.AuditTrailComparisonResult;
import com.danhaywood.cfct.model.AuditTrailEntryDescriptor;
import com.danhaywood.cfct.service.AuditTrailComparisonService;
import com.danhaywood.cfct.spi.AuditTrailEntryReader;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.cfct.webapp.config.WebappDataSources;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Service
public class AutomationAuditTrailComparisonService {

    private final WebappDataSourceConfiguration dataSourceConfiguration;
    private final AuditTrailEntryReader entryReader;
    private final AuditTrailComparisonService comparisonService;

    public AutomationAuditTrailComparisonService(
            final WebappDataSourceConfiguration dataSourceConfiguration,
            final AuditTrailEntryReader entryReader,
            final AuditTrailComparisonService comparisonService) {
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.entryReader = entryReader;
        this.comparisonService = comparisonService;
    }

    public AuditTrailComparisonResult compare(
            final AuthenticatedConnectionContext context,
            final String foregroundInteractionId,
            final Collection<String> appABackgroundInteractionIds,
            final Collection<String> appBBackgroundInteractionIds) {
        final WebappDataSources dataSources = dataSourceConfiguration.dataSourcesFor(context);
        try (Connection appAConnection = dataSources.left().getConnection();
             Connection appBConnection = dataSources.right().getConnection()) {
            final List<AuditTrailEntryDescriptor> appAForeground = entryReader.read(
                    appAConnection, List.of(foregroundInteractionId));
            final List<AuditTrailEntryDescriptor> appBForeground = entryReader.read(
                    appBConnection, List.of(foregroundInteractionId));
            final List<AuditTrailEntryDescriptor> appABackground = entryReader.read(
                    appAConnection, appABackgroundInteractionIds);
            final List<AuditTrailEntryDescriptor> appBBackground = entryReader.read(
                    appBConnection, appBBackgroundInteractionIds);
            return comparisonService.compare(
                    appAForeground,
                    appBForeground,
                    appABackground,
                    appBBackground);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to compare automation audit trails.", ex);
        }
    }
}
