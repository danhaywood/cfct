package com.danhaywood.sqlcomparer.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SqlComparerWebApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SqlComparerWebApplication.class, args);
    }
}
