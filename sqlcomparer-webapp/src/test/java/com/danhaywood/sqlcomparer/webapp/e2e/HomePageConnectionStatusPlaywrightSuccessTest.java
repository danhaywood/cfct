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
                "sqlcomparer.webapp.comparison.validation.fail-fast=true"
        })
@EnabledIfSystemProperty(named = "playwright", matches = "true")
class HomePageConnectionStatusPlaywrightSuccessTest {

    private static final String LEFT_DB = "left_playwright_ok";
    private static final String RIGHT_DB = "right_playwright_ok";

    @LocalServerPort
    private int serverPort;

    @DynamicPropertySource
    static void registerProperties(final DynamicPropertyRegistry registry) {
        PlaywrightSqlServerFixture.createDatabaseIfMissing(LEFT_DB);
        PlaywrightSqlServerFixture.createDatabaseIfMissing(RIGHT_DB);
        PlaywrightSqlServerFixture.prepareManualSelectionTables(LEFT_DB);
        registry.add("sqlcomparer.webapp.comparison.connection.server", PlaywrightSqlServerFixture::server);
        registry.add("sqlcomparer.webapp.comparison.connection.username", PlaywrightSqlServerFixture::username);
        registry.add("sqlcomparer.webapp.comparison.connection.password", PlaywrightSqlServerFixture::password);
        registry.add("sqlcomparer.webapp.comparison.connection.left-database", () -> LEFT_DB);
        registry.add("sqlcomparer.webapp.comparison.connection.right-database", () -> RIGHT_DB);
    }

    @Test
    void showsOkStatusOnHomePage() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.navigate("http://localhost:" + serverPort + "/");
            page.waitForSelector("[data-testid='connection-status-state']");

            final String statusText = page.locator("[data-testid='connection-status-state']").innerText();
            assertThat(statusText).contains("OK");
            assertThat(page.locator("[data-testid='connection-status-summary']").count()).isZero();

            final String feedbackBefore = page.locator("[data-testid='selected-table-feedback']").innerText();
            assertThat(feedbackBefore).contains("Selected tables: 0");

            toggleCheckbox(page, "[data-testid='table-checkbox-dbo-playwrighteligible']");
            page.waitForFunction("() => document.querySelector('[data-testid=\"selected-table-feedback\"]').textContent.includes('Selected tables: 1')");
            final String feedbackAfter = page.locator("[data-testid='selected-table-feedback']").innerText();
            assertThat(feedbackAfter).contains("Selected tables: 1");

            toggleCheckbox(page, "[data-testid='table-checkbox-dbo-playwrightineligible']");
            final String feedbackAfterIneligibleClick = page.locator("[data-testid='selected-table-feedback']").innerText();
            assertThat(feedbackAfterIneligibleClick).contains("Selected tables: 1");
        }
    }

    private void toggleCheckbox(final Page page, final String selector) {
        page.evaluate(
                "(selector) => { const host = document.querySelector(selector); if (!host) return; host.checked = !host.checked; host.dispatchEvent(new CustomEvent('checked-changed', { detail: { value: host.checked }, bubbles: true, composed: true })); host.dispatchEvent(new Event('change', { bubbles: true, composed: true })); }",
                selector);
    }
}
