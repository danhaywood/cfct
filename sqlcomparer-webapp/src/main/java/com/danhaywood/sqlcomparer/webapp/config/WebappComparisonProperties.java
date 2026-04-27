package com.danhaywood.sqlcomparer.webapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps Spring configuration to CLI-equivalent comparison options.
 * <p>
 * Mapping reference:
 * <ul>
 *     <li>server -> CLI -S / --server</li>
 *     <li>username -> CLI -U / --username</li>
 *     <li>password -> CLI -P / --password</li>
 *     <li>left-database -> CLI -l / --left-database</li>
 *     <li>right-database -> CLI -r / --right-database</li>
 *     <li>tables -> CLI -t / --tables</li>
 *     <li>tables-file -> CLI -F / --tables-file</li>
 *     <li>env-file -> CLI -e / --env-file</li>
 *     <li>output-format -> CLI -f / --output-format</li>
 *     <li>output-file -> CLI -o / --output-file</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "sqlcomparer.webapp.comparison")
@Validated
public class WebappComparisonProperties {

    @Valid
    private Connection connection = new Connection();

    @Valid
    private TableSelection tableSelection = new TableSelection();

    @Valid
    private Output output = new Output();

    private String envFile;

    @AssertTrue(message = "Only one table source is allowed: configure either table-selection.tables or table-selection.tables-file")
    public boolean isValidTableSelectionSource() {
        final boolean hasInlineTables = tableSelection != null
                && tableSelection.getTables() != null
                && !tableSelection.getTables().isEmpty();
        final boolean hasTablesFile = tableSelection != null
                && tableSelection.getTablesFile() != null
                && !tableSelection.getTablesFile().isBlank();
        return !(hasInlineTables && hasTablesFile);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(final Connection connection) {
        this.connection = connection;
    }

    public TableSelection getTableSelection() {
        return tableSelection;
    }

    public void setTableSelection(final TableSelection tableSelection) {
        this.tableSelection = tableSelection;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(final Output output) {
        this.output = output;
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

    public static class TableSelection {
        private List<String> tables = new ArrayList<>();
        private String tablesFile;

        public List<String> getTables() {
            return tables;
        }

        public void setTables(final List<String> tables) {
            this.tables = tables;
        }

        public String getTablesFile() {
            return tablesFile;
        }

        public void setTablesFile(final String tablesFile) {
            this.tablesFile = tablesFile;
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
}
