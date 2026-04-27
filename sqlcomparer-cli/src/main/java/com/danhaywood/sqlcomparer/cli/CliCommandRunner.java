package com.danhaywood.sqlcomparer.cli;

import org.springframework.stereotype.Component;

import java.io.PrintStream;

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
            final String report = executor.execute(parsed);
            out.print(report);
            return 0;
        } catch (IllegalArgumentException ex) {
            err.println("Error: " + ex.getMessage());
            return 2;
        } catch (Exception ex) {
            err.println("Error: " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()));
            return 1;
        }
    }
}
