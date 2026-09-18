package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.model.AuditTrailComparisonResult;
import com.danhaywood.cfct.model.AuditTrailEntryDescriptor;
import com.danhaywood.cfct.model.AuditTrailScopeComparison;
import com.danhaywood.cfct.service.AuditTrailComparisonService;
import com.danhaywood.cfct.spi.AuditTrailEntryReader;
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

class AutomationAuditTrailComparisonServiceTest {

    @Test
    void readsForegroundAndEachSidesIndependentBackgroundIds() throws Exception {
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
        final AuditTrailEntryReader reader = mock(AuditTrailEntryReader.class);
        final AuditTrailEntryDescriptor foregroundA = entry("root", "Type:1", "name");
        final AuditTrailEntryDescriptor foregroundB = entry("root", "Type:1", "name");
        final AuditTrailEntryDescriptor backgroundA = entry("left-child", "Type:1", "status");
        final AuditTrailEntryDescriptor backgroundB = entry("right-child", "Type:1", "status");
        when(reader.read(leftConnection, List.of("root"))).thenReturn(List.of(foregroundA));
        when(reader.read(rightConnection, List.of("root"))).thenReturn(List.of(foregroundB));
        when(reader.read(leftConnection, List.of("left-child"))).thenReturn(List.of(backgroundA));
        when(reader.read(rightConnection, List.of("right-child"))).thenReturn(List.of(backgroundB));
        final AuditTrailComparisonService comparisonService = mock(AuditTrailComparisonService.class);
        final AuditTrailScopeComparison clean = new AuditTrailScopeComparison(false, 1, 1, List.of());
        final AuditTrailComparisonResult expected = new AuditTrailComparisonResult(
                false, "semantic-key-counts", clean, clean);
        when(comparisonService.compare(
                List.of(foregroundA),
                List.of(foregroundB),
                List.of(backgroundA),
                List.of(backgroundB))).thenReturn(expected);

        final AuditTrailComparisonResult result = new AutomationAuditTrailComparisonService(
                dataSourceConfiguration,
                reader,
                comparisonService).compare(
                        context,
                        "root",
                        List.of("left-child"),
                        List.of("right-child"));

        assertThat(result).isEqualTo(expected);
        verify(leftConnection).close();
        verify(rightConnection).close();
    }

    private static AuditTrailEntryDescriptor entry(
            final String interactionId,
            final String target,
            final String memberIdentifier) {
        return new AuditTrailEntryDescriptor(interactionId, 0, target, memberIdentifier);
    }
}
