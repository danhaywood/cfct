package com.danhaywood.sqlcomparer.webapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

/**
 * Maps shared Spring configuration to CLI-equivalent execution options.
 * <p>
 * Shared mapping reference:
 * <ul>
 *     <li>connection.server -> CLI -S / --server</li>
 *     <li>connection.username -> CLI -U / --username</li>
 *     <li>connection.password -> CLI -P / --password</li>
 *     <li>connection.left-database -> CLI -l / --left-database</li>
 *     <li>connection.right-database -> CLI -r / --right-database</li>
 *     <li>env-file -> CLI -e / --env-file</li>
 *     <li>output.format -> CLI -f / --output-format</li>
 *     <li>output.file -> CLI -o / --output-file</li>
 * </ul>
 * Table selection is intentionally handled by SelectionPlan strategies and not by these shared properties.
 */
@ConfigurationProperties(prefix = "sqlcomparer.webapp.comparison")
@Validated
public class WebappComparisonProperties {

    @Valid
    private Connection connection = new Connection();

    @Valid
    private Output output = new Output();

    @Valid
    private Validation validation = new Validation();

    private String envFile;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(final Connection connection) {
        this.connection = connection;
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

    public static class Connection {
        private String server;
        private String username;
        private String password;
        private String leftDatabase;
        private String rightDatabase;

        public String getServer() {
            return server;
        }

        public void setServer(final String server) {
            this.server = server;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(final String password) {
            this.password = password;
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

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }
    }
}
