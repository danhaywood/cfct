package com.danhaywood.sqlcomparer.webapp.selection;

import com.danhaywood.sqlcomparer.model.TableRef;
import com.danhaywood.sqlcomparer.spi.CommandAuditTouchedTableResolver;
import com.danhaywood.sqlcomparer.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.sqlcomparer.webapp.auth.AuthenticatedConnectionContextHolder;
import com.danhaywood.sqlcomparer.webapp.config.WebappDataSourceConfiguration;
import com.danhaywood.sqlcomparer.webapp.config.WebappDataSources;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandDrivenTableSelectionServiceTest {

    @Test
    void resolvesTouchedTablesAndIntersectsWithVisibleEligibleRows() throws Exception {
        final CommandAuditTouchedTableResolver resolver = mock(CommandAuditTouchedTableResolver.class);
        final Connection connection = mock(Connection.class);
        final DataSource leftDataSource = mock(DataSource.class);
        when(leftDataSource.getConnection()).thenReturn(connection);

        final WebappDataSourceConfiguration dataSourceConfiguration = mock(WebappDataSourceConfiguration.class);
        final AuthenticatedConnectionContextHolder contextHolder = new AuthenticatedConnectionContextHolder();
        contextHolder.set(new AuthenticatedConnectionContext("localhost:1433", "sa", "pwd", "left_db", "right_db"));

        when(dataSourceConfiguration.dataSourcesFor(Mockito.any()))
                .thenReturn(new WebappDataSources(mock(DataSource.class), leftDataSource, mock(DataSource.class)));

        when(resolver.resolveTouchedQualifiedTableNames(connection, List.of("i1", "i2")))
                .thenReturn(new java.util.TreeSet<>(Set.of("dbo.Supplier", "dbo.Product", "dbo.Missing", "broken")));

        final CommandDrivenTableSelectionService service = new CommandDrivenTableSelectionService(
                dataSourceConfiguration,
                contextHolder,
                resolver);

        final Set<TableRef> resolved = service.resolveTouchedBusinessTables(
                List.of("i1", "i2"),
                List.of(
                        TableCatalogEntry.eligible(new TableRef("dbo", "Supplier")),
                        TableCatalogEntry.ineligible(new TableRef("dbo", "Product"), "No unique index ending with _PK."),
                        TableCatalogEntry.eligible(new TableRef("dbo", "PurchaseOrder"))));

        assertThat(resolved).containsExactly(new TableRef("dbo", "Supplier"));
    }

    @Test
    void parsesQualifiedNamesDefensively() {
        assertThat(CommandDrivenTableSelectionService.parseQualifiedName("dbo.Supplier"))
                .contains(new TableRef("dbo", "Supplier"));
        assertThat(CommandDrivenTableSelectionService.parseQualifiedName("dbo."))
                .isEmpty();
        assertThat(CommandDrivenTableSelectionService.parseQualifiedName("Supplier"))
                .isEmpty();
        assertThat(CommandDrivenTableSelectionService.parseQualifiedName(" "))
                .isEmpty();
    }
}
