package com.danhaywood.cfct.sql;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

import javax.sql.DataSource;

public final class TracingDataSourceProxyFactory {

    private TracingDataSourceProxyFactory() {
    }

    public static DataSource wrapIfEnabled(
            final DataSource dataSource,
            final String dataSourceName,
            final boolean traceEnabled) {
        if (!traceEnabled) {
            return dataSource;
        }
        return ProxyDataSourceBuilder.create(dataSource)
                .name(dataSourceName)
                .logQueryBySlf4j(SLF4JLogLevel.INFO)
                .multiline()
                .build();
    }
}
