package com.danhaywood.cfct.core;

import com.danhaywood.cfct.SqlComparerApplication;
import com.danhaywood.cfct.cli.CliArgumentsParser;
import com.danhaywood.cfct.cli.CliCommandRunner;
import com.danhaywood.cfct.cli.CliComparisonExecutor;
import com.danhaywood.cfct.service.MultiTableComparisonReportFormatter;
import com.danhaywood.cfct.service.MultiTableComparisonService;
import com.danhaywood.cfct.service.TableComparisonService;
import com.danhaywood.cfct.spi.TableMetadataReader;
import com.danhaywood.cfct.spi.TableRowReader;
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
