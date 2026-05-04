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

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "cfct.webapp.validation.enabled=true",
                "cfct.webapp.validation.fail-fast=true"
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
        PlaywrightSqlServerFixture.prepareManualSelectionTables(RIGHT_DB);
        registry.add("spring.datasource.url", PlaywrightSqlServerFixture::jdbcUrl);
        registry.add("spring.datasource.driver-class-name", () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registry.add("spring.datasource.username", PlaywrightSqlServerFixture::username);
        registry.add("spring.datasource.password", PlaywrightSqlServerFixture::password);
        registry.add("cfct.webapp.connection.left-database", () -> LEFT_DB);
        registry.add("cfct.webapp.connection.right-database", () -> RIGHT_DB);
    }

    @Test
    void keyboardFocusStartsOnFirstCommandRowAndSpaceToggleFollowsArrowNavigation() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            page.navigate("http://localhost:" + serverPort + "/");
            page.waitForSelector("[data-testid='login-submit']");
            fillLoginForm(page, LEFT_DB, RIGHT_DB);
            page.click("[data-testid='login-submit']");
            page.waitForSelector("[data-testid='command-selection-grid']");

            final String firstCommandCheckbox = "[data-testid='command-checkbox-" + PlaywrightSqlServerFixture.COMMAND_INTERACTION_ID.toLowerCase() + "']";
            final String secondCommandCheckbox = "[data-testid='command-checkbox-" + PlaywrightSqlServerFixture.SECOND_COMMAND_INTERACTION_ID.toLowerCase() + "']";

            page.waitForFunction("([first, second]) => !document.querySelector(first).checked && !document.querySelector(second).checked", List.of(firstCommandCheckbox, secondCommandCheckbox));

            page.keyboard().press("Tab");
            page.keyboard().press("Tab");
            page.keyboard().press("Space");
            page.waitForFunction("([first, second]) => document.querySelector(first).checked === true && document.querySelector(second).checked !== true", List.of(firstCommandCheckbox, secondCommandCheckbox));

            page.keyboard().press("ArrowDown");
            page.keyboard().press("Space");
            page.waitForFunction("([first, second]) => document.querySelector(first).checked === true && document.querySelector(second).checked === true", List.of(firstCommandCheckbox, secondCommandCheckbox));

            page.keyboard().press("Space");
            page.waitForFunction("([first, second]) => document.querySelector(first).checked === true && document.querySelector(second).checked !== true", List.of(firstCommandCheckbox, secondCommandCheckbox));

            page.keyboard().press("ArrowUp");
            page.keyboard().press("Space");
            page.waitForFunction("([first, second]) => document.querySelector(first).checked !== true && document.querySelector(second).checked !== true", List.of(firstCommandCheckbox, secondCommandCheckbox));
        }
    }

    @Test
    void pressingSpaceTogglesFocusedBusinessRowsAndArrowNavigationKeepsFlowStable() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            page.navigate("http://localhost:" + serverPort + "/");
            page.waitForSelector("[data-testid='login-submit']");
            fillLoginForm(page, LEFT_DB, RIGHT_DB);
            page.click("[data-testid='login-submit']");
            page.waitForSelector("[data-testid='table-selection-grid']");
            setSelectedOnly(page, false);

            final String supplierCheckbox = "[data-testid='table-checkbox-dbo-supplier']";
            final String productCheckbox = "[data-testid='table-checkbox-dbo-product']";

            page.waitForFunction("([first, second]) => !document.querySelector(first).checked && !document.querySelector(second).checked", List.of(supplierCheckbox, productCheckbox));

            page.locator(supplierCheckbox).click();
            page.keyboard().press("Space");
            page.waitForFunction("([first, second]) => document.querySelector(first).checked === true && document.querySelector(second).checked !== true", List.of(supplierCheckbox, productCheckbox));

            page.keyboard().press("ArrowDown");
            page.keyboard().press("Space");
            page.waitForFunction("([first, second]) => document.querySelector(first).checked === true && document.querySelector(second).checked === true", List.of(supplierCheckbox, productCheckbox));

            page.keyboard().press("ArrowUp");
            page.keyboard().press("Space");
            page.waitForFunction("([first, second]) => document.querySelector(first).checked !== true && document.querySelector(second).checked === true", List.of(supplierCheckbox, productCheckbox));

            assertThat(page.locator("[data-testid='navigation-compare-action-bar'] [data-testid='compare-button']").isEnabled()).isTrue();
        }
    }

    @Test
    void pressingEnterRunsCompareAfterCommandDrivenSelectionEnablesCompare() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            page.navigate("http://localhost:" + serverPort + "/");
            page.waitForSelector("[data-testid='login-submit']");
            fillLoginForm(page, LEFT_DB, RIGHT_DB);
            page.click("[data-testid='login-submit']");
            page.waitForSelector("[data-testid='command-selection-grid']");

            toggleCheckbox(page, "[data-testid='command-checkbox-" + PlaywrightSqlServerFixture.COMMAND_INTERACTION_ID.toLowerCase() + "']");
            page.waitForFunction("() => !document.querySelector('[data-testid=\"compare-button\"]').disabled");

            page.keyboard().press("Enter");
            page.waitForSelector("[data-testid='comparison-results-tabs']");
            assertThat(page.locator("[data-testid^='comparison-result-tab-']").count()).isGreaterThan(0);
        }
    }

    @Test
    void showsOkStatusAndMainUiHappyPathOnHomePage() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            page.navigate("http://localhost:" + serverPort + "/");
            page.waitForSelector("[data-testid='login-submit']");
            assertThat(page.locator("[data-testid='navigation-compare-action-bar'] [data-testid='compare-button']").isDisabled()).isTrue();
            fillLoginForm(page, LEFT_DB, RIGHT_DB);
            page.click("[data-testid='login-submit']");
            page.waitForSelector("[data-testid='connection-status-state']");
            page.waitForSelector("[data-testid='command-selection-grid']");
            page.waitForSelector("[data-testid='table-selection-grid']");

            final String statusText = page.locator("[data-testid='connection-status-state']").innerText();
            assertThat(statusText).contains("OK");
            assertThat(page.locator("[data-testid='connection-status-summary']").count()).isZero();

            assertThat(page.locator("[data-testid='hamburger-menu']").count()).isEqualTo(1);
            final String footerText = page.locator("[data-testid='connection-details-footer']").innerText();
            assertThat(footerText).contains(LEFT_DB, RIGHT_DB, "Status: OK");
            assertThat(footerText).doesNotContain(PlaywrightSqlServerFixture.jdbcUrl(), PlaywrightSqlServerFixture.password(), "SQL connectivity status");

            assertThat(page.locator("[data-testid='selected-table-feedback']").count()).isZero();
            assertThat(page.locator("[data-testid='navigation-compare-action-bar'] [data-testid='compare-button']").isDisabled()).isTrue();
            assertThat(page.locator("[data-testid='clear-selections-button']").isDisabled()).isTrue();
            assertThat(page.locator("[data-testid='apply-table-filter']").count()).isZero();
            assertThat(page.locator("[data-testid='account-menu']").count()).isEqualTo(1);
            assertThat(page.locator("[data-testid='account-menu-label']").innerText()).contains(PlaywrightSqlServerFixture.username());
            assertThat(page.locator("[data-testid='logout-button']").count()).isZero();

            assertThat(page.locator("[data-testid='command-selection-spacer']").count()).isEqualTo(1);

            final double memberFilterTop = positionOf(page, "[data-testid='command-filter-member-id']")[1];
            final double interactionFilterTop = positionOf(page, "[data-testid='command-filter-interaction-id']")[1];
            final double commandGridTop = positionOf(page, "[data-testid='command-selection-grid']")[1];
            final double compareTop = positionOf(page, "[data-testid='navigation-compare-action-bar']")[1];
            final double tableGridTop = positionOf(page, "[data-testid='table-selection-grid']")[1];
            assertThat(Math.abs(memberFilterTop - interactionFilterTop)).isLessThan(8.0);
            assertThat(memberFilterTop).isLessThan(commandGridTop);
            assertThat(commandGridTop).isLessThan(tableGridTop);
            assertThat(compareTop).isGreaterThan(tableGridTop);

            final String commandGridText = page.locator("[data-testid='command-selection-grid']").innerText();
            assertThat(commandGridText).contains(
                    "Timestamp",
                    "Member",
                    "Interaction",
                    PlaywrightSqlServerFixture.COMMAND_INTERACTION_ID,
                    PlaywrightSqlServerFixture.SECOND_COMMAND_INTERACTION_ID);
            assertThat(commandGridText.indexOf("Timestamp")).isLessThan(commandGridText.indexOf("Member"));
            assertThat(commandGridText.indexOf("Member")).isLessThan(commandGridText.indexOf("Interaction"));

            final double[] footerMetrics = footerStatusAlignmentMetrics(page);
            assertThat(footerMetrics[0]).isGreaterThan(footerMetrics[1]);

            final CheckboxState selectedOnlyState = selectedOnlyState(page);
            assertThat(selectedOnlyState.checked()).isTrue();

            final String initialGridText = page.locator("[data-testid='table-selection-grid']").innerText();
            assertThat(initialGridText).doesNotContain("Supplier", "Product", "PurchaseOrderWithoutBusinessKey");
            assertThat(initialGridText).doesNotContain("CommandLogEntry", "AuditTrailEntry", "LogicalTypeTableMapping");

            setSelectedOnly(page, false);
            page.waitForFunction("() => document.querySelector('[data-testid=\"table-selection-grid\"]').innerText.includes('Supplier')");
            assertThat(page.locator("[data-testid='table-checkbox-dbo-purchaseorderwithoutbusinesskey']").getAttribute("disabled")).isNotNull();
            assertThat(page.locator("[data-testid='table-checkbox-dbo-purchaseorderwithoutbusinesskey']").getAttribute("title")).isNotBlank();

            page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath("webapp-main.png")).setFullPage(true));

            page.click("[data-testid='hamburger-menu']");
            page.waitForTimeout(200);
            page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath("webapp-collapsed.png")).setFullPage(true));

            page.click("[data-testid='hamburger-menu']");
            page.waitForTimeout(200);

            page.setViewportSize(900, 560);
            page.waitForTimeout(200);
            final Object compareVisibleAfterResize = page.evaluate("() => { const bar = document.querySelector('[data-testid=\"navigation-compare-action-bar\"]'); if (!bar) return false; const r = bar.getBoundingClientRect(); return r.top >= 0 && r.bottom <= window.innerHeight; }");
            assertThat(compareVisibleAfterResize).isEqualTo(Boolean.TRUE);

            setCommandMemberFilter(page, "supplier.Supplier");
            page.waitForFunction("() => document.querySelector('[data-testid=\"command-selection-grid\"]').innerText.includes('supplier.Supplier#registerProduct') && !document.querySelector('[data-testid=\"command-selection-grid\"]').innerText.includes('product.Product#changeStatus')");
            setCommandInteractionFilter(page, PlaywrightSqlServerFixture.COMMAND_INTERACTION_ID.substring(0, 8));
            page.waitForFunction("() => document.querySelector('[data-testid=\"command-selection-grid\"]').innerText.includes('11111111-1111-1111-1111-111111111111')");
            setCommandMemberFilter(page, "");
            setCommandInteractionFilter(page, "");

            toggleCheckbox(page, "[data-testid='command-checkbox-" + PlaywrightSqlServerFixture.COMMAND_INTERACTION_ID.toLowerCase() + "']");
            page.waitForFunction("() => document.querySelector('[data-testid=\"table-checkbox-dbo-supplier\"]').checked === true");
            page.waitForFunction("() => document.querySelector('[data-testid=\"table-checkbox-dbo-product\"]').checked !== true");
            assertThat(page.locator("[data-testid='clear-selections-button']").isEnabled()).isTrue();

            toggleCheckbox(page, "[data-testid='command-checkbox-" + PlaywrightSqlServerFixture.SECOND_COMMAND_INTERACTION_ID.toLowerCase() + "']");
            page.waitForFunction("() => document.querySelector('[data-testid=\"table-checkbox-dbo-supplier\"]').checked === true");
            page.waitForFunction("() => document.querySelector('[data-testid=\"table-checkbox-dbo-product\"]').checked === true");

            page.click("[data-testid='clear-selections-button']");
            page.waitForFunction("() => document.querySelector('[data-testid=\"command-checkbox-11111111-1111-1111-1111-111111111111\"]').checked !== true");
            page.waitForFunction("() => document.querySelector('[data-testid=\"command-checkbox-22222222-2222-2222-2222-222222222222\"]').checked !== true");
            assertThat(page.locator("[data-testid='clear-selections-button']").isDisabled()).isTrue();

            setSelectedOnly(page, false);
            setFilter(page, PlaywrightSqlServerFixture.ELIGIBLE_TABLE);
            page.waitForFunction("() => document.querySelector('[data-testid=\"table-selection-grid\"]').innerText.includes('Supplier') && !document.querySelector('[data-testid=\"table-selection-grid\"]').innerText.includes('PurchaseOrderWithoutBusinessKey')");
            final String filteredText = page.locator("[data-testid='table-selection-grid']").innerText();
            assertThat(filteredText).contains("Supplier");
            assertThat(filteredText).doesNotContain("PurchaseOrderWithoutBusinessKey");

            setFilter(page, "");
            page.waitForFunction("() => document.querySelector('[data-testid=\"table-selection-grid\"]').innerText.includes('PurchaseOrderWithoutBusinessKey')");
            page.locator("vaadin-grid-sorter[aria-label='Sort by Table']").click();
            page.waitForTimeout(250);
            final String sortedGridText = page.locator("[data-testid='table-selection-grid']").innerText();
            assertThat(sortedGridText.indexOf("PurchaseOrderWithoutBusinessKey")).isLessThan(sortedGridText.indexOf("Supplier"));

            selectAllEligibleTables(page);
            page.waitForFunction("() => !document.querySelector('[data-testid=\"compare-button\"]').disabled");
            assertThat(page.locator("[data-testid='navigation-compare-action-bar'] [data-testid='compare-button']").isEnabled()).isTrue();

            page.click("[data-testid='compare-button']");
            page.waitForSelector("[data-testid='comparison-results-tabs']");
            assertThat(page.locator("[data-testid='comparison-differences-only-filter']").isChecked()).isTrue();
            assertThat(page.locator("[data-testid^='comparison-result-tab-']").count()).isEqualTo(2);
            assertThat(page.locator("[data-testid='comparison-result-tab-dbo-supplier']").getAttribute("data-has-differences")).isEqualTo("true");
            assertThat(page.locator("[data-testid='comparison-result-tab-dbo-product']").getAttribute("data-has-differences")).isEqualTo("true");
            assertThat(page.locator("[data-testid='comparison-result-tab-dbo-customeraddress']").getAttribute("data-has-differences")).isEqualTo("false");

            toggleCheckbox(page, "[data-testid='comparison-differences-only-filter']");
            page.waitForFunction("() => document.querySelectorAll('[data-testid^=\"comparison-result-tab-\"]').length === 3");
            assertThat(page.locator("[data-testid='comparison-result-tab-dbo-customeraddress']").count()).isEqualTo(1);

            assertThat(page.locator("[data-testid^='comparison-grid-dbo-']").count()).isEqualTo(1);
            final String gridText = page.locator("[data-testid^='comparison-grid-dbo-']").first().innerText();
            assertThat(gridText).contains("Business Key", "Status", "name");
            assertThat(gridText).doesNotContain("L:", "R:");
            assertThat(page.locator("[data-testid='comparison-table-filter']").count()).isEqualTo(1);
            assertThat(page.locator("[data-testid='comparison-differences-only-filter']").count()).isEqualTo(1);
            assertThat(page.locator("[data-testid='download-format-select']").count()).isEqualTo(1);
            assertThat(page.locator("[data-testid='download-action']").count()).isEqualTo(1);

            page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath("webapp-selected.png")).setFullPage(true));
        }
    }


    private void fillLoginForm(final Page page, final String leftDb, final String rightDb) {
        setLoginField(page, "login-jdbc-url", PlaywrightSqlServerFixture.jdbcUrl());
        setLoginField(page, "login-username", PlaywrightSqlServerFixture.username());
        setLoginField(page, "login-password", PlaywrightSqlServerFixture.password());
        setLoginField(page, "login-left-database", leftDb);
        setLoginField(page, "login-right-database", rightDb);
    }

    private void setLoginField(final Page page, final String testId, final String value) {
        page.evaluate("([id, val]) => { const host = document.querySelector(`[data-testid='${id}']`); if (!host) return; host.value = val; host.dispatchEvent(new Event('input', { bubbles: true, composed: true })); host.dispatchEvent(new Event('change', { bubbles: true, composed: true })); }", List.of(testId, value));
    }

    private void toggleCheckbox(final Page page, final String selector) {
        page.evaluate(
                "(selector) => { const host = document.querySelector(selector); if (!host) return; host.checked = !host.checked; host.dispatchEvent(new CustomEvent('checked-changed', { detail: { value: host.checked }, bubbles: true, composed: true })); host.dispatchEvent(new Event('change', { bubbles: true, composed: true })); }",
                selector);
    }

    private void selectAllEligibleTables(final Page page) {
        page.evaluate("() => { document.querySelectorAll('[data-testid^=\"table-checkbox-\"]').forEach((host) => { if (host.hasAttribute('disabled')) return; host.checked = true; host.dispatchEvent(new CustomEvent('checked-changed', { detail: { value: true }, bubbles: true, composed: true })); host.dispatchEvent(new Event('change', { bubbles: true, composed: true })); }); }");
    }

    private void setFilter(final Page page, final String value) {
        page.locator("[data-testid='table-filter-table'] input").fill(value);
        page.keyboard().press("Tab");
    }

    private void setSelectedOnly(final Page page, final boolean checked) {
        final CheckboxState state = selectedOnlyState(page);
        if (state.exists() && state.checked() != checked) {
            toggleCheckbox(page, "[data-testid='selected-only-checkbox']");
        }
    }

    private CheckboxState selectedOnlyState(final Page page) {
        final Object evaluated = page.evaluate("() => { const host = document.querySelector('[data-testid=\"selected-only-checkbox\"]'); return host ? { exists: true, checked: !!host.checked } : { exists: false, checked: false }; }");
        if (evaluated instanceof java.util.Map<?, ?> map) {
            final boolean exists = Boolean.TRUE.equals(map.get("exists"));
            final boolean checked = Boolean.TRUE.equals(map.get("checked"));
            return new CheckboxState(exists, checked);
        }
        return new CheckboxState(false, false);
    }

    private void setCommandMemberFilter(final Page page, final String value) {
        page.locator("[data-testid='command-filter-member-id'] input").fill(value);
        page.keyboard().press("Tab");
    }

    private void setCommandInteractionFilter(final Page page, final String value) {
        page.locator("[data-testid='command-filter-interaction-id'] input").fill(value);
        page.keyboard().press("Tab");
    }

    private Path screenshotPath(final String fileName) {
        final Path cwd = Path.of("").toAbsolutePath();
        final Path repoDocs = cwd.resolve("../docs/images").normalize();
        final Path localDocs = cwd.resolve("docs/images").normalize();
        if (repoDocs.toFile().exists()) {
            return repoDocs.resolve(fileName);
        }
        return localDocs.resolve(fileName);
    }

    private double[] positionOf(final Page page, final String selector) {
        final Object evaluated = page.evaluate("(selector) => { const r = document.querySelector(selector)?.getBoundingClientRect(); return r ? [r.left, r.top, r.right, r.bottom] : [-1, -1, -1, -1]; }", selector);
        return toDoubleArray((List<?>) evaluated);
    }

    private double[] footerStatusAlignmentMetrics(final Page page) {
        final Object evaluated = page.evaluate("() => { const footer = document.querySelector('[data-testid=\"connection-details-footer\"]'); const state = document.querySelector('[data-testid=\"connection-status-state\"]'); if (!footer || !state) return [-1, -1]; const fr = footer.getBoundingClientRect(); const sr = state.getBoundingClientRect(); return [sr.right, fr.left + (fr.width / 2)]; }");
        return toDoubleArray((List<?>) evaluated);
    }

    private double[] toDoubleArray(final List<?> values) {
        final double[] array = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = ((Number) values.get(i)).doubleValue();
        }
        return array;
    }

    private record CheckboxState(boolean exists, boolean checked) {
    }
}
