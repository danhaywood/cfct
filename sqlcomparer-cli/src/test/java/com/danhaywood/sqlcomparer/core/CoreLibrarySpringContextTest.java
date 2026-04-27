package com.danhaywood.sqlcomparer.core;

import com.danhaywood.sqlcomparer.SqlComparerApplication;
import com.danhaywood.sqlcomparer.comparison.MultiTableComparer;
import com.danhaywood.sqlcomparer.comparison.TableComparer;
import com.danhaywood.sqlcomparer.config.ConfiguredComparisonService;
import com.danhaywood.sqlcomparer.config.JsonComparisonRequestLoader;
import com.danhaywood.sqlcomparer.report.JsonMultiTableComparisonReportRenderer;
import com.danhaywood.sqlcomparer.spi.TableMetadataReader;
import com.danhaywood.sqlcomparer.spi.TableRowReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class CoreLibrarySpringContextTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SqlComparerApplication.class);

    @Test
    void coreComparisonServicesCanBeSpringManaged() {
        contextRunner.run(context -> assertThat(context)
                .hasSingleBean(TableComparer.class)
                .hasSingleBean(MultiTableComparer.class)
                .hasSingleBean(JsonComparisonRequestLoader.class)
                .hasSingleBean(ConfiguredComparisonService.class)
                .hasSingleBean(JsonMultiTableComparisonReportRenderer.class)
                .hasSingleBean(ObjectMapper.class)
                .hasSingleBean(TableMetadataReader.class)
                .hasSingleBean(TableRowReader.class));
    }
}
