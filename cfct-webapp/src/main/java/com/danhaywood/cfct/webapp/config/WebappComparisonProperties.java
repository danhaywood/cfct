package com.danhaywood.cfct.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

/**
 * Maps shared Spring configuration to CLI-equivalent execution options.
 */
@ConfigurationProperties(prefix = "cfct.webapp.comparison")
@Validated
public class WebappComparisonProperties {

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.driver-class-name:com.microsoft.sqlserver.jdbc.SQLServerDriver}")
    private String datasourceDriverClassName;

    @Value("${spring.datasource.username:sa}")
    private String datasourceUsername;

    @Value("${spring.datasource.password:change-me}")
    private String datasourcePassword;

    @Valid
    private Output output = new Output();

    @Valid
    private Validation validation = new Validation();

    private String envFile;
    private String leftDatabase;
    private String rightDatabase;

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public void setDatasourceUrl(final String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    public String getDatasourceDriverClassName() {
        return datasourceDriverClassName;
    }

    public void setDatasourceDriverClassName(final String datasourceDriverClassName) {
        this.datasourceDriverClassName = datasourceDriverClassName;
    }

    public String getDatasourceUsername() {
        return datasourceUsername;
    }

    public void setDatasourceUsername(final String datasourceUsername) {
        this.datasourceUsername = datasourceUsername;
    }

    public String getDatasourcePassword() {
        return datasourcePassword;
    }

    public void setDatasourcePassword(final String datasourcePassword) {
        this.datasourcePassword = datasourcePassword;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(final Output output) {
        this.output = output;
    }

    public Validation getValidation() {
        return validation;
    }

    public void setValidation(final Validation validation) {
        this.validation = validation;
    }

    public String getEnvFile() {
        return envFile;
    }

    public void setEnvFile(final String envFile) {
        this.envFile = envFile;
    }

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

    public static class Output {
        private String format;
        private String file;

        public String getFormat() {
            return format;
        }

        public void setFormat(final String format) {
            this.format = format;
        }

        public String getFile() {
            return file;
        }

        public void setFile(final String file) {
            this.file = file;
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
