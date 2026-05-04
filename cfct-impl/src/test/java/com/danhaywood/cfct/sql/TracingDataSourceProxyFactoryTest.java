package com.danhaywood.cfct.sql;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TracingDataSourceProxyFactoryTest {

    @Test
    void returnsOriginalDataSourceWhenTracingDisabled() {
        final DataSource dataSource = mock(DataSource.class);

        final DataSource wrapped = TracingDataSourceProxyFactory.wrapIfEnabled(dataSource, "test", false);

        assertThat(wrapped).isSameAs(dataSource);
    }

    @Test
    void wrapsDataSourceWhenTracingEnabled() {
        final DataSource dataSource = mock(DataSource.class);

        final DataSource wrapped = TracingDataSourceProxyFactory.wrapIfEnabled(dataSource, "test", true);

        assertThat(wrapped).isNotSameAs(dataSource);
    }
}
