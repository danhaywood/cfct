package com.danhaywood.sqlcomparer.core;

import com.danhaywood.sqlcomparer.SqlComparerApplication;
import com.danhaywood.sqlcomparer.cli.CliArgumentsParser;
import com.danhaywood.sqlcomparer.cli.CliCommandRunner;
import com.danhaywood.sqlcomparer.cli.CliComparisonExecutor;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonReportFormatter;
import com.danhaywood.sqlcomparer.service.MultiTableComparisonService;
import com.danhaywood.sqlcomparer.service.TableComparisonService;
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
                .hasSingleBean(CliArgumentsParser.class)
                .hasSingleBean(CliCommandRunner.class)
                .hasSingleBean(CliComparisonExecutor.class)
                .hasSingleBean(TableComparisonService.class)
                .hasSingleBean(MultiTableComparisonService.class)
                .hasSingleBean(MultiTableComparisonReportFormatter.class)
                .hasSingleBean(ObjectMapper.class)
                .hasSingleBean(TableMetadataReader.class)
                .hasSingleBean(TableRowReader.class));
    }
}
