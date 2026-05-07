package com.danhaywood.cfct.webapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@ConfigurationProperties(prefix = "cfct.webapp")
@Validated
public class WebappComparisonProperties {

    @Valid
    private Connection connection = new Connection();

    @Valid
    private Validation validation = new Validation();

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(final Connection connection) {
        this.connection = connection;
    }

    public Validation getValidation() {
        return validation;
    }

    public void setValidation(final Validation validation) {
        this.validation = validation;
    }

    public static class Connection {
        private String leftDatabase;
        private String rightDatabase;

        public String getLeftDatabase() {
            return leftDatabase;
        }

        public void setLeftDatabase(final String leftDatabase) {
            this.leftDatabase = leftDatabase;
        }

        public String getRightDatabase() {
            return rightDatabase;
        }

        public void setRightDatabase(final String rightDatabase) {
            this.rightDatabase = rightDatabase;
        }
    }

    public static class Validation {
        private boolean enabled = true;
        private boolean failFast = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isFailFast() {
            return failFast;
        }

        public void setFailFast(final boolean failFast) {
            this.failFast = failFast;
        }
    }
}
