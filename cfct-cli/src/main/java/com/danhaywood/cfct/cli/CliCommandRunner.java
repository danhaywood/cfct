package com.danhaywood.cfct.cli;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

@Component
public final class CliCommandRunner {

    private final CliArgumentsParser parser;
    private final CliComparisonExecutor executor;

    public CliCommandRunner(final CliArgumentsParser parser, final CliComparisonExecutor executor) {
        this.parser = parser;
        this.executor = executor;
    }

    public int run(final String[] args) {
        return run(args, System.out, System.err);
    }

    int run(final String[] args, final PrintStream out, final PrintStream err) {
        try {
            final CliArguments parsed = parser.parse(args);
            final CliExecutionOutput output = executor.execute(parsed, err);
            writeOutput(parsed, output, out);
            return 0;
        } catch (IllegalArgumentException ex) {
            err.println("Error: " + ex.getMessage());
            return 2;
        } catch (Exception ex) {
            err.println("Error: " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()));
            return 1;
        }
    }

    private void writeOutput(final CliArguments arguments, final CliExecutionOutput output, final PrintStream out) throws IOException {
        final byte[] bytes = output.bytes();
        if (arguments.outputFile() != null) {
            Files.write(arguments.outputFile(), bytes);
            return;
        }
        out.write(bytes, 0, bytes.length);
        out.flush();
    }
}
