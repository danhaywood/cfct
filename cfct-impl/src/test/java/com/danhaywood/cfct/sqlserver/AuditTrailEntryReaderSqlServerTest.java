package com.danhaywood.cfct.sqlserver;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class AuditTrailEntryReaderSqlServerTest {

    @Test
    void emptyInteractionCollectionDoesNotAccessTheConnection() {
        final Connection connection = mock(Connection.class);

        assertThat(new AuditTrailEntryReaderSqlServer().read(connection, List.of())).isEmpty();

        verifyNoInteractions(connection);
    }
}
