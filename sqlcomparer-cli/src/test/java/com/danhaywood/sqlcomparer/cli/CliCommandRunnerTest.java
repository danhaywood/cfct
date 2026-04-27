package com.danhaywood.sqlcomparer.cli;

import com.danhaywood.sqlcomparer.model.TableRef;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
    void writesSuccessfulOutputToFileWhenOutputFileIsProvided(@TempDir final Path tempDir) {
        final RecordingExecutor executor = new RecordingExecutor();
        final CliCommandRunner runner = new CliCommandRunner(new CliArgumentsParser(), executor);
        final ByteArrayOutputStream stdoutBytes = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderrBytes = new ByteArrayOutputStream();
        final Path outputFile = tempDir.resolve("comparison.txt");

        final int exitCode = runner.run(new String[]{
                        "-S", "server-host",
                        "-U", "sa",
                        "-P", "secret",
                        "-l", "left_db",
                        "-r", "right_db",
                        "-t", "dbo.Supplier",
                        "-o", outputFile.toString()
                },
                new PrintStream(stdoutBytes, true, StandardCharsets.UTF_8),
                new PrintStream(stderrBytes, true, StandardCharsets.UTF_8));

        assertThat(exitCode).isEqualTo(0);
        assertThat(stdoutBytes.toString(StandardCharsets.UTF_8)).isEmpty();
        assertThat(stderrBytes.toString(StandardCharsets.UTF_8)).isEmpty();
        assertThat(outputFile).hasContent("report-output");
    }

    @Test
    void rejectsExcelOutputWithoutOutputFileBeforeExecutorRuns() {
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
                        "-t", "dbo.Supplier",
                        "--output-format", "excel"
                },
                new PrintStream(stdoutBytes, true, StandardCharsets.UTF_8),
                new PrintStream(stderrBytes, true, StandardCharsets.UTF_8));

        assertThat(exitCode).isEqualTo(2);
        assertThat(stdoutBytes.toString(StandardCharsets.UTF_8)).isEmpty();
        assertThat(stderrBytes.toString(StandardCharsets.UTF_8)).contains("Error:").contains("-o").contains("excel");
        assertThat(executor.receivedArguments).isNull();
    }

    @Test
    void writesSuccessfulOutputBytesWithoutTextEncodingConversion(@TempDir final Path tempDir) {
        final byte[] binaryOutput = new byte[]{0x50, 0x4b, 0x03, 0x04, 0x00, (byte) 0xff};
        final CliComparisonExecutor executor = arguments -> new CliExecutionOutput(
                CliOutputFormat.EXCEL,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "xlsx",
                binaryOutput);
        final CliCommandRunner runner = new CliCommandRunner(new CliArgumentsParser(), executor);
        final ByteArrayOutputStream stdoutBytes = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderrBytes = new ByteArrayOutputStream();
        final Path outputFile = tempDir.resolve("comparison.xlsx");

        final int exitCode = runner.run(new String[]{
                        "-S", "server-host",
                        "-U", "sa",
                        "-P", "secret",
                        "-l", "left_db",
                        "-r", "right_db",
                        "-t", "dbo.Supplier",
                        "--output-format", "excel",
                        "-o", outputFile.toString()
                },
                new PrintStream(stdoutBytes, true, StandardCharsets.UTF_8),
                new PrintStream(stderrBytes, true, StandardCharsets.UTF_8));

        assertThat(exitCode).isEqualTo(0);
        assertThat(stdoutBytes.toByteArray()).isEmpty();
        assertThat(stderrBytes.toString(StandardCharsets.UTF_8)).isEmpty();
        assertThat(outputFile).hasBinaryContent(binaryOutput);
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
        public CliExecutionOutput execute(final CliArguments arguments) {
            this.receivedArguments = arguments;
            return CliExecutionOutput.text("report-output");
        }
    }
}
