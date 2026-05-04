package com.danhaywood.cfct.webapp.e2e;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "cfct.webapp.comparison.validation.enabled=true",
                "cfct.webapp.comparison.validation.fail-fast=false"
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
        registry.add("spring.datasource.url", PlaywrightSqlServerFixture::jdbcUrl);
        registry.add("spring.datasource.driver-class-name", () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registry.add("spring.datasource.username", PlaywrightSqlServerFixture::username);
        registry.add("spring.datasource.password", PlaywrightSqlServerFixture::password);
        registry.add("cfct.webapp.comparison.left-database", () -> LEFT_DB);
        registry.add("cfct.webapp.comparison.right-database", () -> MISSING_DB);
    }

    @Test
    void showsFailedStatusAndSummaryOnLogin() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.navigate("http://localhost:" + serverPort + "/");
            page.waitForSelector("[data-testid='login-submit']");
            fillLoginForm(page);
            page.click("[data-testid='login-submit']");
            page.waitForSelector("[data-testid='login-error']");

            final String summary = page.locator("[data-testid='login-error']").innerText();
            assertThat(summary).contains("Configured database does not exist");
        }
    }

    private void fillLoginForm(final Page page) {
        setLoginField(page, "login-jdbc-url", PlaywrightSqlServerFixture.jdbcUrl());
        setLoginField(page, "login-username", PlaywrightSqlServerFixture.username());
        setLoginField(page, "login-password", PlaywrightSqlServerFixture.password());
        setLoginField(page, "login-left-database", LEFT_DB);
        setLoginField(page, "login-right-database", MISSING_DB);
    }

    private void setLoginField(final Page page, final String testId, final String value) {
        page.evaluate("([id, val]) => { const host = document.querySelector(`[data-testid='${id}']`); if (!host) return; host.value = val; host.dispatchEvent(new Event('input', { bubbles: true, composed: true })); host.dispatchEvent(new Event('change', { bubbles: true, composed: true })); }", List.of(testId, value));
    }

}
