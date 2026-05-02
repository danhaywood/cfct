package com.danhaywood.cfct.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ComparisonOutputTypeTest {

    @Test
    void parsesJsonOutputType() {
        assertThat(ComparisonOutputType.parse("json")).isEqualTo(ComparisonOutputType.JSON);
        assertThat(ComparisonOutputType.parse(" JSON ")).isEqualTo(ComparisonOutputType.JSON);
    }

    @Test
    void parsesYamlOutputType() {
        assertThat(ComparisonOutputType.parse("yaml")).isEqualTo(ComparisonOutputType.YAML);
        assertThat(ComparisonOutputType.parse(" YAML ")).isEqualTo(ComparisonOutputType.YAML);
    }

    @Test
    void parsesExcelOutputType() {
        assertThat(ComparisonOutputType.parse("excel")).isEqualTo(ComparisonOutputType.EXCEL);
        assertThat(ComparisonOutputType.parse(" EXCEL ")).isEqualTo(ComparisonOutputType.EXCEL);
    }

    @Test
    void rejectsBlankOutputType() {
        assertThatThrownBy(() -> ComparisonOutputType.parse(" "))
                .isInstanceOf(ComparisonRequestException.class)
                .hasMessageContaining("Comparison output type is required");
    }

    @Test
    void rejectsUnsupportedOutputType() {
        assertThatThrownBy(() -> ComparisonOutputType.parse("text"))
                .isInstanceOf(ComparisonRequestException.class)
                .hasMessageContaining("Unsupported comparison output type: text");
    }
}
