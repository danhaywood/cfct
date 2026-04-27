package com.danhaywood.sqlcomparer.webapp.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "sqlcomparer.webapp.comparison.validation.enabled=true",
                "sqlcomparer.webapp.comparison.validation.fail-fast=false"
        })
@EnabledIfSystemProperty(named = "playwright", matches = "true")
class HomePageConnectionStatusPlaywrightFailureTest {

    private static final String LEFT_DB = "left_playwright_failure";
    private static final String MISSING_DB = "missing_db_playwright_should_not_exist";

    @LocalServerPort
    private int serverPort;

    @DynamicPropertySource
    static void registerProperties(final DynamicPropertyRegistry registry) {
        PlaywrightSqlServerFixture.createDatabaseIfMissing(LEFT_DB);
        PlaywrightSqlServerFixture.prepareManualSelectionTables(LEFT_DB);
        registry.add("sqlcomparer.webapp.comparison.connection.server", PlaywrightSqlServerFixture::server);
        registry.add("sqlcomparer.webapp.comparison.connection.username", PlaywrightSqlServerFixture::username);
        registry.add("sqlcomparer.webapp.comparison.connection.password", PlaywrightSqlServerFixture::password);
        registry.add("sqlcomparer.webapp.comparison.connection.left-database", () -> LEFT_DB);
        registry.add("sqlcomparer.webapp.comparison.connection.right-database", () -> MISSING_DB);
    }

    @Test
    void showsFailedStatusAndSummaryOnHomePage() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.navigate("http://localhost:" + serverPort + "/");
            page.waitForSelector("[data-testid='connection-status-state']");
            page.waitForSelector("[data-testid='connection-status-summary']");

            final String statusText = page.locator("[data-testid='connection-status-state']").innerText();
            final String summary = page.locator("[data-testid='connection-status-summary']").innerText();
            assertThat(statusText).contains("FAILED");
            assertThat(summary).contains("Configured database does not exist");
        }
    }
}
