package com.danhaywood.cfct;

import com.danhaywood.cfct.cli.CliCommandRunner;
import com.danhaywood.cfct.implspring.ComparisonImplementationConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "com.danhaywood.cfct.cli")
@Import(ComparisonImplementationConfiguration.class)
public class CfctApplication {

    public static void main(final String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(CfctApplication.class, args);
        final int exitCode;
        try {
            exitCode = context.getBean(CliCommandRunner.class).run(args);
        } finally {
            context.close();
        }
        System.exit(exitCode);
    }
}
