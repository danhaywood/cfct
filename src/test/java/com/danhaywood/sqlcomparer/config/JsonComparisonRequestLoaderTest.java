package com.danhaywood.sqlcomparer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonComparisonRequestLoaderTest {

    private final JsonComparisonRequestLoader loader = new JsonComparisonRequestLoader(new ObjectMapper());

    @Test
    void loadsValidRequest() {
        final JsonComparisonRequest request = load("""
                {
                  "output": { "type": "json" },
                  "tables": [ { "schema": "dbo", "name": "Supplier" } ]
                }
                """);

        assertThat(request.outputType()).isEqualTo(ComparisonOutputType.JSON);
        assertThat(request.toMultiTableComparisonRequest().tables())
                .extracting(table -> table.schemaName() + "." + table.tableName())
                .containsExactly("dbo.Supplier");
    }

    @Test
    void rejectsMissingOutputType() {
        assertThatThrownBy(() -> load("""
                {
                  "tables": [ { "schema": "dbo", "name": "Supplier" } ]
                }
                """))
                .isInstanceOf(ComparisonRequestException.class)
                .hasMessageContaining("output type");
    }

    @Test
    void rejectsEmptyOutputType() {
        assertThatThrownBy(() -> load("""
                {
                  "output": { "type": "" },
                  "tables": [ { "schema": "dbo", "name": "Supplier" } ]
                }
                """))
                .isInstanceOf(ComparisonRequestException.class)
                .hasMessageContaining("output type");
    }

    @Test
    void rejectsUnsupportedOutputType() {
        assertThatThrownBy(() -> load("""
                {
                  "output": { "type": "text" },
                  "tables": [ { "schema": "dbo", "name": "Supplier" } ]
                }
                """))
                .isInstanceOf(ComparisonRequestException.class)
                .hasMessageContaining("Unsupported comparison output type: text");
    }

    @Test
    void rejectsEmptyTableList() {
        assertThatThrownBy(() -> load("""
                {
                  "output": { "type": "json" },
                  "tables": []
                }
                """))
                .isInstanceOf(ComparisonRequestException.class)
                .hasMessageContaining("At least one table");
    }

    private JsonComparisonRequest load(final String json) {
        return loader.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }
}
