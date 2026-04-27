package com.danhaywood.sqlcomparer.cli;

public interface CliComparisonExecutor {

    CliExecutionOutput execute(CliArguments arguments) throws Exception;
}
