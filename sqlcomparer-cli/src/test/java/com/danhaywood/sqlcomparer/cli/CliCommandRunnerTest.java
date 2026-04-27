package com.danhaywood.sqlcomparer.cli;

import com.danhaywood.sqlcomparer.model.TableRef;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CliCommandRunnerTest {

    @Test
    void runsSuccessfullyAndPrintsReportToStdout() {
        final RecordingExecutor executor = new RecordingExecutor();
        final CliCommandRunner runner = new CliCommandRunner(new CliArgumentsParser(), executor);
        final ByteArrayOutputStream stdoutBytes = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderrBytes = new ByteArrayOutputStream();

        final int exitCode = runner.run(new String[]{
                        "-S", "server-host",
                        "-U", "sa",
                        "-P", "secret",
                        "-l", "left_db",
                        "-r", "right_db",
                        "-t", "dbo.Supplier,dbo.PurchaseOrder"
                },
                new PrintStream(stdoutBytes, true, StandardCharsets.UTF_8),
                new PrintStream(stderrBytes, true, StandardCharsets.UTF_8));

        assertThat(exitCode).isEqualTo(0);
        assertThat(stdoutBytes.toString(StandardCharsets.UTF_8)).isEqualTo("report-output");
        assertThat(stderrBytes.toString(StandardCharsets.UTF_8)).isEmpty();
        assertThat(executor.receivedArguments).isNotNull();
        assertThat(executor.receivedArguments.tables()).extracting(TableRef::displayName)
                .containsExactly("dbo.Supplier", "dbo.PurchaseOrder");
    }

    @Test
    void returnsValidationErrorCodeAndMessageForInvalidArguments() {
        final CliCommandRunner runner = new CliCommandRunner(new CliArgumentsParser(), new RecordingExecutor());
        final ByteArrayOutputStream stdoutBytes = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderrBytes = new ByteArrayOutputStream();

        final int exitCode = runner.run(new String[]{
                        "-S", "server-host",
                        "-U", "sa",
                        "-P", "secret",
                        "-l", "left_db"
                },
                new PrintStream(stdoutBytes, true, StandardCharsets.UTF_8),
                new PrintStream(stderrBytes, true, StandardCharsets.UTF_8));

        assertThat(exitCode).isEqualTo(2);
        assertThat(stdoutBytes.toString(StandardCharsets.UTF_8)).isEmpty();
        assertThat(stderrBytes.toString(StandardCharsets.UTF_8)).contains("Error:").contains("-r");
    }

    @Test
    void returnsExecutionErrorCodeAndMessageWhenExecutorFails() {
        final CliComparisonExecutor failingExecutor = arguments -> {
            throw new IllegalStateException("comparison failed");
        };
        final CliCommandRunner runner = new CliCommandRunner(new CliArgumentsParser(), failingExecutor);
        final ByteArrayOutputStream stdoutBytes = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderrBytes = new ByteArrayOutputStream();

        final int exitCode = runner.run(new String[]{
                        "-S", "server-host",
                        "-U", "sa",
                        "-P", "secret",
                        "-l", "left_db",
                        "-r", "right_db",
                        "-t", "dbo.Supplier"
                },
                new PrintStream(stdoutBytes, true, StandardCharsets.UTF_8),
                new PrintStream(stderrBytes, true, StandardCharsets.UTF_8));

        assertThat(exitCode).isEqualTo(1);
        assertThat(stdoutBytes.toString(StandardCharsets.UTF_8)).isEmpty();
        assertThat(stderrBytes.toString(StandardCharsets.UTF_8)).contains("Error:").contains("comparison failed");
    }

    private static final class RecordingExecutor implements CliComparisonExecutor {

        private CliArguments receivedArguments;

        @Override
        public String execute(final CliArguments arguments) {
            this.receivedArguments = arguments;
            return "report-output";
        }
    }
}
