package com.danhaywood.cfct.webapp.config;

import com.danhaywood.cfct.implspring.SqlTraceProperties;
import com.danhaywood.cfct.sql.TracingDataSourceProxyFactory;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebappDataSourceConfiguration {

    private final boolean sqlTraceEnabled;

    public WebappDataSourceConfiguration(final SqlTraceProperties sqlTraceProperties) {
        this.sqlTraceEnabled = sqlTraceProperties.isEnabled();
    }

    public WebappDataSources dataSourcesFor(final AuthenticatedConnectionContext context) {
        return new WebappDataSources(
                sqlServerDataSource(context.jdbcUrl(), context.jdbcDriver(), context.username(), context.password(), "master", "webapp-master"),
                sqlServerDataSource(context.jdbcUrl(), context.jdbcDriver(), context.username(), context.password(), context.leftDatabase(), "webapp-left"),
                sqlServerDataSource(context.jdbcUrl(), context.jdbcDriver(), context.username(), context.password(), context.rightDatabase(), "webapp-right"));
    }

    private DataSource sqlServerDataSource(
            final String jdbcUrl,
            final String jdbcDriver,
            final String username,
            final String password,
            final String databaseName,
            final String dataSourceName) {
        final SQLServerDataSource dataSource = new SQLServerDataSource();
        applyJdbcUrl(dataSource, jdbcUrl, databaseName);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        if (jdbcDriver != null && !jdbcDriver.isBlank()) {
            dataSource.setSelectMethod("direct");
        }
        return TracingDataSourceProxyFactory.wrapIfEnabled(dataSource, dataSourceName, sqlTraceEnabled);
    }

    private static void applyJdbcUrl(final SQLServerDataSource dataSource, final String jdbcUrl, final String databaseName) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return;
        }
        final String trimmed = jdbcUrl.trim();
        final Pattern pattern = Pattern.compile("^jdbc:sqlserver://([^;]+)(?:;(.*))?$");
        final Matcher matcher = pattern.matcher(trimmed);
        if (!matcher.matches()) {
            dataSource.setURL(trimmed);
            return;
        }
        final String hostPort = matcher.group(1);
        final String props = matcher.group(2);
        final int sep = hostPort.lastIndexOf(':');
        if (sep > 0 && sep < hostPort.length() - 1) {
            dataSource.setServerName(hostPort.substring(0, sep));
            dataSource.setPortNumber(Integer.parseInt(hostPort.substring(sep + 1)));
        } else {
            dataSource.setServerName(hostPort);
        }
        dataSource.setDatabaseName(databaseName);
        dataSource.setURL(trimmed + (props != null && props.contains("databaseName=") ? "" : ";databaseName=" + databaseName));
    }
}
