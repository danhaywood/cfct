package com.danhaywood.cfct.webapp.e2e;

import com.danhaywood.cfct.webapp.e2e.pageobjects.LoginPageObject;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "cfct.webapp.validation.enabled=true",
                "cfct.webapp.validation.fail-fast=false",
                "cfct.webapp.connection.left-database=left_playwright_failure",
                "cfct.webapp.connection.right-database=missing_db_playwright_should_not_exist"
        })
@Import(PlaywrightE2eTestConfiguration.class)
@EnabledIfSystemProperty(named = "playwright", matches = "true")
class HomePageConnectionStatusPlaywrightFailureTest {

    private static final String LEFT_DB = "left_playwright_failure";
    private static final String MISSING_DB = "missing_db_playwright_should_not_exist";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private PlaywrightSqlServerFixture fixture;

    @DynamicPropertySource
    static void registerProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PlaywrightSqlServerContainerSupport::jdbcUrl);
        registry.add("spring.datasource.driver-class-name", () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registry.add("spring.datasource.username", PlaywrightSqlServerContainerSupport::username);
        registry.add("spring.datasource.password", PlaywrightSqlServerContainerSupport::password);
    }

    @BeforeEach
    void prepareFixture() {
        fixture.createDatabaseIfMissing(LEFT_DB);
        fixture.prepareManualSelectionTables(LEFT_DB);
    }

    @Test
    void showsFailedStatusAndSummaryOnLogin() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            final LoginPageObject loginPage = new LoginPageObject(page);
            loginPage.open("http://localhost:" + serverPort);
            loginPage.login(fixture.jdbcUrl(), fixture.username(), fixture.password(), LEFT_DB, MISSING_DB);

            final String summary = loginPage.loginErrorText();
            assertThat(summary).contains("Configured database does not exist");
        }
    }
}
