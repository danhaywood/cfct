package com.danhaywood.sqlcomparer.core;

import com.danhaywood.sqlcomparer.SqlComparerApplication;
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
                .hasSingleBean(TableMetadataReader.class)
                .hasSingleBean(TableRowReader.class));
    }
}
