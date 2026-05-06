package com.danhaywood.cfct.webapp.e2e;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
class PlaywrightE2eTestConfiguration {

    @Bean
    PlaywrightSqlServerFixture playwrightSqlServerFixture() {
        return new PlaywrightSqlServerFixture(PlaywrightSqlServerContainerSupport.sqlServer());
    }
}
