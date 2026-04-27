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
    void showsOkStatusAndMainUiHappyPathOnHomePage() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.navigate("http://localhost:" + serverPort + "/");
            page.waitForSelector("[data-testid='connection-status-state']");
            page.waitForSelector("[data-testid='table-selection-grid']");

            final String statusText = page.locator("[data-testid='connection-status-state']").innerText();
            assertThat(statusText).contains("OK");
            assertThat(page.locator("[data-testid='connection-status-summary']").count()).isZero();

            assertThat(page.locator("[data-testid='hamburger-menu']").count()).isEqualTo(1);
            final String footerText = page.locator("[data-testid='connection-details-footer']").innerText();
            assertThat(footerText).contains(PlaywrightSqlServerFixture.server(), LEFT_DB, RIGHT_DB, "SQL connectivity status");
            assertThat(footerText).doesNotContain(PlaywrightSqlServerFixture.password());

            assertThat(page.locator("[data-testid='selected-table-feedback']").count()).isZero();
            assertThat(page.locator("[data-testid='comparison-action-bar'] [data-testid='compare-button']").isDisabled()).isTrue();
            assertThat(page.locator("[data-testid='apply-table-filter']").count()).isZero();

            final String initialGridText = page.locator("[data-testid='table-selection-grid']").innerText();
            assertThat(initialGridText).doesNotContain("Eligibility");
            assertThat(page.locator("[data-testid='table-checkbox-dbo-playwrightineligible']").getAttribute("disabled")).isNotNull();
            assertThat(page.locator("[data-testid='table-checkbox-dbo-playwrightineligible']").getAttribute("title")).isNotBlank();

            setFilter(page, PlaywrightSqlServerFixture.ELIGIBLE_TABLE);
            page.waitForFunction("() => document.querySelector('[data-testid=\"table-selection-grid\"]').innerText.includes('PlaywrightEligible') && !document.querySelector('[data-testid=\"table-selection-grid\"]').innerText.includes('PlaywrightIneligible')");
            final String filteredText = page.locator("[data-testid='table-selection-grid']").innerText();
            assertThat(filteredText).contains("PlaywrightEligible");
            assertThat(filteredText).doesNotContain("PlaywrightIneligible");

            setFilter(page, "");
            page.waitForFunction("() => document.querySelector('[data-testid=\"table-selection-grid\"]').innerText.includes('PlaywrightIneligible')");
            page.locator("vaadin-grid-sorter[aria-label='Sort by Table']").click();
            page.waitForTimeout(250);
            final String sortedGridText = page.locator("[data-testid='table-selection-grid']").innerText();
            assertThat(sortedGridText.indexOf("PlaywrightEligible")).isLessThan(sortedGridText.indexOf("PlaywrightIneligible"));

            toggleCheckbox(page, "[data-testid='table-checkbox-dbo-playwrighteligible']");
            page.waitForFunction("() => !document.querySelector('[data-testid=\"compare-button\"]').disabled");
            assertThat(page.locator("[data-testid='comparison-action-bar'] [data-testid='compare-button']").isEnabled()).isTrue();

            toggleCheckbox(page, "[data-testid='table-checkbox-dbo-playwrightineligible']");
            assertThat(page.locator("[data-testid='comparison-action-bar'] [data-testid='compare-button']").isEnabled()).isTrue();
        }
    }

    private void toggleCheckbox(final Page page, final String selector) {
        page.evaluate(
                "(selector) => { const host = document.querySelector(selector); if (!host) return; host.checked = !host.checked; host.dispatchEvent(new CustomEvent('checked-changed', { detail: { value: host.checked }, bubbles: true, composed: true })); host.dispatchEvent(new Event('change', { bubbles: true, composed: true })); }",
                selector);
    }

    private void setFilter(final Page page, final String value) {
        page.locator("[data-testid='table-filter-table'] input").fill(value);
        page.keyboard().press("Tab");
    }
}
