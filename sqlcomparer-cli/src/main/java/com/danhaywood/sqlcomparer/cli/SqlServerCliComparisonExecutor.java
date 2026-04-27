package com.danhaywood.sqlcomparer.cli;

import com.danhaywood.sqlcomparer.comparison.MultiTableComparer;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;
import com.danhaywood.sqlcomparer.report.TextMultiTableComparisonReportRenderer;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;

@Service
public final class SqlServerCliComparisonExecutor implements CliComparisonExecutor {

    private final MultiTableComparer comparer;
    private final TextMultiTableComparisonReportRenderer renderer;

    public SqlServerCliComparisonExecutor(
            final MultiTableComparer comparer,
            final TextMultiTableComparisonReportRenderer renderer) {
        this.comparer = comparer;
        this.renderer = renderer;
    }

    @Override
    public String execute(final CliArguments arguments) throws Exception {
        final String leftJdbcUrl = jdbcUrl(arguments.server(), arguments.leftDatabase());
        final String rightJdbcUrl = jdbcUrl(arguments.server(), arguments.rightDatabase());
        try (Connection leftConnection = DriverManager.getConnection(leftJdbcUrl, arguments.username(), arguments.password());
             Connection rightConnection = DriverManager.getConnection(rightJdbcUrl, arguments.username(), arguments.password())) {
            final var result = comparer.compare(
                    leftConnection,
                    rightConnection,
                    MultiTableComparisonRequest.forTables(arguments.tables()));
            return renderer.render(result);
        }
    }

    private String jdbcUrl(final String server, final String databaseName) {
        return "jdbc:sqlserver://%s;databaseName=%s;encrypt=false;trustServerCertificate=true"
                .formatted(server, databaseName);
    }
}
