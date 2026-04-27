package com.danhaywood.sqlcomparer;

import com.danhaywood.sqlcomparer.cli.CliCommandRunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SqlComparerApplication {

    public static void main(final String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SqlComparerApplication.class, args);
        final int exitCode;
        try {
            exitCode = context.getBean(CliCommandRunner.class).run(args);
        } finally {
            context.close();
        }
        System.exit(exitCode);
    }
}
