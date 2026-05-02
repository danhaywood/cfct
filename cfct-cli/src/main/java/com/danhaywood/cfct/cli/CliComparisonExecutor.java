package com.danhaywood.cfct.cli;

import java.io.PrintStream;

public interface CliComparisonExecutor {

    CliExecutionOutput execute(CliArguments arguments) throws Exception;

    default CliExecutionOutput execute(final CliArguments arguments, final PrintStream err) throws Exception {
        return execute(arguments);
    }
}
