package com.danhaywood.sqlcomparer.cli;

import org.junit.jupiter.api.Test;

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
}
