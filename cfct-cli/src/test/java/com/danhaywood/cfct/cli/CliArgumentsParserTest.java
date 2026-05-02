package com.danhaywood.cfct.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CliArgumentsParserTest {

    private final CliArgumentsParser parser = new CliArgumentsParser();

    @Test
    void parsesRequiredArgumentsAndOrderedTables() {
        final CliArguments arguments = parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "-t", "dbo.Supplier,dbo.PurchaseOrder"
        });

        assertThat(arguments.server()).isEqualTo("server-host");
        assertThat(arguments.username()).isEqualTo("sa");
        assertThat(arguments.password()).isEqualTo("secret");
        assertThat(arguments.leftDatabase()).isEqualTo("left_db");
        assertThat(arguments.rightDatabase()).isEqualTo("right_db");
        assertThat(arguments.tables()).extracting(table -> table.displayName())
                .containsExactly("dbo.Supplier", "dbo.PurchaseOrder");
        assertThat(arguments.outputFormat()).isEqualTo(CliOutputFormat.TEXT);
        assertThat(arguments.outputFile()).isNull();
    }

    @Test
    void parsesOptionalOutputFile() {
        final CliArguments arguments = parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "-t", "dbo.Supplier",
                "-o", "target/comparison.json"
        });

        assertThat(arguments.outputFile()).hasToString("target/comparison.json");
    }

    @Test
    void requiresOutputFileForExcelOutput() {
        assertThatThrownBy(() -> parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "-t", "dbo.Supplier",
                "--output-format", "excel"
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-o")
                .hasMessageContaining("excel");
    }

    @Test
    void acceptsExcelOutputWhenOutputFileIsProvided() {
        final CliArguments arguments = parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "-t", "dbo.Supplier",
                "--output-format", "excel",
                "-o", "comparison.xlsx"
        });

        assertThat(arguments.outputFormat()).isEqualTo(CliOutputFormat.EXCEL);
        assertThat(arguments.outputFile()).hasToString("comparison.xlsx");
    }

    @Test
    void supportsShortFlagsForLongOptions(@TempDir final Path tempDir) throws IOException {
        final Path envFile = writeEnvFile(tempDir, """
                SQLCOMPARER_SERVER=server-from-env
                SQLCOMPARER_USERNAME=user-from-env
                SQLCOMPARER_PASSWORD=password-from-env
                SQLCOMPARER_LEFT_DATABASE=left_from_env
                SQLCOMPARER_RIGHT_DATABASE=right_from_env
                """);
        final Path tablesFile = tempDir.resolve("tables.txt");
        java.nio.file.Files.writeString(tablesFile, "dbo.Supplier\ndbo.PurchaseOrder\n");

        final CliArguments arguments = parser.parse(new String[]{
                "-e", envFile.toString(),
                "-F", tablesFile.toString(),
                "-f", "json",
                "-o", "comparison.json"
        });

        assertThat(arguments.server()).isEqualTo("server-from-env");
        assertThat(arguments.outputFormat()).isEqualTo(CliOutputFormat.JSON);
        assertThat(arguments.outputFile()).hasToString("comparison.json");
        assertThat(arguments.tables()).extracting(table -> table.displayName())
                .containsExactly("dbo.Supplier", "dbo.PurchaseOrder");
    }

    @Test
    void parsesSupportedNonExcelOutputFormatsWithoutOutputFile() {
        for (CliOutputFormat outputFormat : List.of(CliOutputFormat.TEXT, CliOutputFormat.JSON, CliOutputFormat.YAML)) {
            final CliArguments arguments = parser.parse(new String[]{
                    "-S", "server-host",
                    "-U", "sa",
                    "-P", "secret",
                    "-l", "left_db",
                    "-r", "right_db",
                    "-t", "dbo.Supplier",
                    "--output-format", outputFormat.argumentValue()
            });

            assertThat(arguments.outputFormat()).isEqualTo(outputFormat);
        }
    }

    @Test
    void rejectsUnsupportedOutputFormat() {
        assertThatThrownBy(() -> parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "-t", "dbo.Supplier",
                "--output-format", "pdf"
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported output format")
                .hasMessageContaining("pdf")
                .hasMessageContaining("text, json, yaml, excel");
    }

    @Test
    void rejectsMissingRequiredArguments() {
        assertThatThrownBy(() -> parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-t", "dbo.Supplier"
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-r");
    }

    @Test
    void rejectsUnknownArgument() {
        assertThatThrownBy(() -> parser.parse(new String[]{
                "-S", "server-host",
                "--unknown", "value",
                "-t", "dbo.Supplier"
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown option")
                .hasMessageContaining("--unknown");
    }

    @Test
    void rejectsMissingValueForNewOptions() {
        assertThatThrownBy(() -> parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "--tables-file"
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("--tables-file");
    }

    @Test
    void rejectsMalformedTableToken() {
        assertThatThrownBy(() -> parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "-t", "dbo.Supplier,brokenToken"
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("brokenToken")
                .hasMessageContaining("schema.table");
    }

    @Test
    void rejectsBlankTableTokenFromTrailingComma() {
        assertThatThrownBy(() -> parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "-t", "dbo.Supplier,"
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("blank token");
    }

    @Test
    void parsesTablesFileInOrder(@TempDir final Path tempDir) throws IOException {
        final Path tablesFile = tempDir.resolve("tables.txt");
        java.nio.file.Files.writeString(tablesFile, "dbo.Supplier\ndbo.PurchaseOrder\n");

        final CliArguments arguments = parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "--tables-file", tablesFile.toString()
        });

        assertThat(arguments.tables()).extracting(table -> table.displayName())
                .containsExactly("dbo.Supplier", "dbo.PurchaseOrder");
    }

    @Test
    void rejectsBlankLineInTablesFile(@TempDir final Path tempDir) throws IOException {
        final Path tablesFile = tempDir.resolve("tables.txt");
        java.nio.file.Files.writeString(tablesFile, "dbo.Supplier\n\ndbo.PurchaseOrder\n");

        assertThatThrownBy(() -> parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "--tables-file", tablesFile.toString()
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("blank line")
                .hasMessageContaining("line 2");
    }

    @Test
    void rejectsMalformedLineInTablesFile(@TempDir final Path tempDir) throws IOException {
        final Path tablesFile = tempDir.resolve("tables.txt");
        java.nio.file.Files.writeString(tablesFile, "dbo.Supplier\nbrokenToken\n");

        assertThatThrownBy(() -> parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "--tables-file", tablesFile.toString()
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("brokenToken")
                .hasMessageContaining("schema.table");
    }

    @Test
    void rejectsConflictingTableSources(@TempDir final Path tempDir) throws IOException {
        final Path tablesFile = tempDir.resolve("tables.txt");
        java.nio.file.Files.writeString(tablesFile, "dbo.Supplier\n");

        assertThatThrownBy(() -> parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "-t", "dbo.Supplier",
                "--tables-file", tablesFile.toString()
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only one table source is allowed");
    }

    @Test
    void resolvesConnectionValuesFromEnvFile(@TempDir final Path tempDir) throws IOException {
        final Path envFile = writeEnvFile(tempDir, """
                # local comparison defaults
                SQLCOMPARER_SERVER=server-from-env
                SQLCOMPARER_USERNAME=user-from-env
                SQLCOMPARER_PASSWORD=password-from-env
                SQLCOMPARER_LEFT_DATABASE=left_from_env
                SQLCOMPARER_RIGHT_DATABASE=right_from_env
                """);

        final CliArguments arguments = parser.parse(new String[]{
                "--env-file", envFile.toString(),
                "-t", "dbo.Supplier"
        });

        assertThat(arguments.server()).isEqualTo("server-from-env");
        assertThat(arguments.username()).isEqualTo("user-from-env");
        assertThat(arguments.password()).isEqualTo("password-from-env");
        assertThat(arguments.leftDatabase()).isEqualTo("left_from_env");
        assertThat(arguments.rightDatabase()).isEqualTo("right_from_env");
    }

    @Test
    void commandLineValuesOverrideEnvFileValues(@TempDir final Path tempDir) throws IOException {
        final Path envFile = writeEnvFile(tempDir, """
                SQLCOMPARER_SERVER=server-from-env
                SQLCOMPARER_USERNAME=user-from-env
                SQLCOMPARER_PASSWORD=password-from-env
                SQLCOMPARER_LEFT_DATABASE=left_from_env
                SQLCOMPARER_RIGHT_DATABASE=right_from_env
                """);

        final CliArguments arguments = parser.parse(new String[]{
                "--env-file", envFile.toString(),
                "-S", "server-from-cli",
                "-P", "password-from-cli",
                "-t", "dbo.Supplier"
        });

        assertThat(arguments.server()).isEqualTo("server-from-cli");
        assertThat(arguments.username()).isEqualTo("user-from-env");
        assertThat(arguments.password()).isEqualTo("password-from-cli");
        assertThat(arguments.leftDatabase()).isEqualTo("left_from_env");
        assertThat(arguments.rightDatabase()).isEqualTo("right_from_env");
    }

    @Test
    void missingDefaultEnvFileIsIgnoredWhenCommandLineValuesAreComplete() {
        final CliArguments arguments = parser.parse(new String[]{
                "-S", "server-host",
                "-U", "sa",
                "-P", "secret",
                "-l", "left_db",
                "-r", "right_db",
                "-t", "dbo.Supplier"
        });

        assertThat(arguments.server()).isEqualTo("server-host");
        assertThat(arguments.tables()).extracting(table -> table.displayName())
                .containsExactly("dbo.Supplier");
    }

    @Test
    void loadsDefaultEnvFileFromWorkingDirectory(@TempDir final Path tempDir) throws IOException {
        writeEnvFile(tempDir, """
                SQLCOMPARER_SERVER=server-from-default-env
                SQLCOMPARER_USERNAME=user-from-default-env
                SQLCOMPARER_PASSWORD=password-from-default-env
                SQLCOMPARER_LEFT_DATABASE=left_from_default_env
                SQLCOMPARER_RIGHT_DATABASE=right_from_default_env
                """);
        final String originalUserDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.toString());

            final CliArguments arguments = parser.parse(new String[]{
                    "-t", "dbo.Supplier"
            });

            assertThat(arguments.server()).isEqualTo("server-from-default-env");
            assertThat(arguments.username()).isEqualTo("user-from-default-env");
            assertThat(arguments.password()).isEqualTo("password-from-default-env");
            assertThat(arguments.leftDatabase()).isEqualTo("left_from_default_env");
            assertThat(arguments.rightDatabase()).isEqualTo("right_from_default_env");
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void rejectsUnresolvedConnectionValue(@TempDir final Path tempDir) throws IOException {
        final Path envFile = writeEnvFile(tempDir, """
                SQLCOMPARER_SERVER=server-from-env
                SQLCOMPARER_USERNAME=user-from-env
                SQLCOMPARER_PASSWORD=password-from-env
                SQLCOMPARER_LEFT_DATABASE=left_from_env
                """);

        assertThatThrownBy(() -> parser.parse(new String[]{
                "--env-file", envFile.toString(),
                "-t", "dbo.Supplier"
        }))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-r");
    }

    private Path writeEnvFile(final Path directory, final String contents) throws IOException {
        final Path envFile = directory.resolve(".env");
        java.nio.file.Files.writeString(envFile, contents);
        return envFile;
    }
}
