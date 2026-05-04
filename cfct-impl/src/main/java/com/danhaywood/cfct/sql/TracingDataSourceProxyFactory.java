package com.danhaywood.cfct.sql;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

import javax.sql.DataSource;
import java.util.Locale;

public final class TracingDataSourceProxyFactory {

    private static final String ENV_SQL_TRACE_ENABLED = "CFCT_SQL_TRACE_ENABLED";
    private static final String PROP_SQL_TRACE_ENABLED = "cfct.sql.trace.enabled";

    private TracingDataSourceProxyFactory() {
    }

    public static DataSource wrapIfEnabled(final DataSource dataSource, final String dataSourceName) {
        if (!isEnabled()) {
            return dataSource;
        }
        return ProxyDataSourceBuilder.create(dataSource)
                .name(dataSourceName)
                .logQueryBySlf4j(SLF4JLogLevel.INFO)
                .multiline()
                .build();
    }

    private static boolean isEnabled() {
        final String prop = System.getProperty(PROP_SQL_TRACE_ENABLED);
        if (prop != null && !prop.isBlank()) {
            return isTruthy(prop);
        }
        final String env = System.getenv(ENV_SQL_TRACE_ENABLED);
        return env != null && isTruthy(env);
    }

    private static boolean isTruthy(final String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "1", "true", "yes", "y", "on" -> true;
            default -> false;
        };
    }
}
