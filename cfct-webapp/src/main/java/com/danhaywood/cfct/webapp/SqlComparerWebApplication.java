package com.danhaywood.cfct.webapp;

import com.danhaywood.cfct.implspring.ComparisonImplementationConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ConfigurationPropertiesScan
@Import(ComparisonImplementationConfiguration.class)
public class SqlComparerWebApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SqlComparerWebApplication.class, args);
    }
}
