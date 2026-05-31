package com.danhaywood.cfct.webapp.e2e;

import com.danhaywood.cfct.webapp.e2e.pageobjects.ComparisonPageObject;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "cfct.webapp.validation.enabled=true",
                "cfct.webapp.validation.fail-fast=true",
                "cfct.webapp.connection.left-database=left_playwright_ok",
                "cfct.webapp.connection.right-database=right_playwright_ok"
        })
@Import(PlaywrightE2eTestConfiguration.class)
@EnabledIfSystemProperty(named = "playwright", matches = "true")
class HomePageConnectionStatusPlaywrightSuccessTest {

    // Keep scenario interactions and waits routed through page objects.
    // Avoid direct Playwright Page selector choreography in test flows.

    private static final String LEFT_DB = "left_playwright_ok";
    private static final String RIGHT_DB = "right_playwright_ok";

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
        fixture.createDatabaseIfMissing(RIGHT_DB);
        fixture.prepareManualSelectionTables(LEFT_DB);
        fixture.prepareManualSelectionTables(RIGHT_DB);
    }

    @Test
    void keyboardFocusStartsOnFirstCommandRowAndSpaceToggleFollowsArrowNavigation() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            final LoginPageObject loginPage = new LoginPageObject(page);
            final ComparisonPageObject comparisonPage = new ComparisonPageObject(page);
            loginPage.open("http://localhost:" + serverPort);
            loginPage.login(fixture.jdbcUrl(), fixture.username(), fixture.password(), LEFT_DB, RIGHT_DB);
            comparisonPage.waitForCommandSelectionGrid();

            comparisonPage.waitForCommandSelectionState(fixture.commandInteractionId(), false, fixture.secondCommandInteractionId(), false);
            comparisonPage.pressKey("Tab");
            comparisonPage.pressKey("Tab");
            comparisonPage.pressKey("Space");
            comparisonPage.waitForCommandSelectionState(fixture.commandInteractionId(), true, fixture.secondCommandInteractionId(), false);

            comparisonPage.pressKey("ArrowDown");
            comparisonPage.pressKey("Space");
            comparisonPage.waitForCommandSelectionState(fixture.commandInteractionId(), true, fixture.secondCommandInteractionId(), true);

            comparisonPage.pressKey("Space");
            comparisonPage.waitForCommandSelectionState(fixture.commandInteractionId(), true, fixture.secondCommandInteractionId(), false);

            comparisonPage.pressKey("ArrowUp");
            comparisonPage.pressKey("Space");
            comparisonPage.waitForCommandSelectionState(fixture.commandInteractionId(), false, fixture.secondCommandInteractionId(), false);
        }
    }

    @Test
    void shiftClickAndShiftSpaceSelectContiguousCommandRange() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            final LoginPageObject loginPage = new LoginPageObject(page);
            final ComparisonPageObject comparisonPage = new ComparisonPageObject(page);
            loginPage.open("http://localhost:" + serverPort);
            loginPage.login(fixture.jdbcUrl(), fixture.username(), fixture.password(), LEFT_DB, RIGHT_DB);
            comparisonPage.waitForCommandSelectionGrid();

            comparisonPage.waitForCommandSelectionState(fixture.commandInteractionId(), false, fixture.secondCommandInteractionId(), false);
            comparisonPage.clickCommandCheckbox(fixture.commandInteractionId());
            comparisonPage.waitForCommandSelectionState(fixture.commandInteractionId(), true, fixture.secondCommandInteractionId(), false);

            comparisonPage.shiftClickCommandCheckbox(fixture.secondCommandInteractionId());
            comparisonPage.waitForCommandSelectionState(fixture.commandInteractionId(), true, fixture.secondCommandInteractionId(), true);

            comparisonPage.clickCommandCheckbox(fixture.commandInteractionId());
            comparisonPage.waitForCommandSelectionState(fixture.commandInteractionId(), false, fixture.secondCommandInteractionId(), true);

            comparisonPage.pressKey("ArrowDown");
            comparisonPage.pressShiftSpace();
            comparisonPage.waitForCommandSelectionState(fixture.commandInteractionId(), true, fixture.secondCommandInteractionId(), true);
        }
    }

    @Test
    void pressingSpaceTogglesFocusedBusinessRowsAndArrowNavigationKeepsFlowStable() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            final LoginPageObject loginPage = new LoginPageObject(page);
            final ComparisonPageObject comparisonPage = new ComparisonPageObject(page);
            loginPage.open("http://localhost:" + serverPort);
            loginPage.login(fixture.jdbcUrl(), fixture.username(), fixture.password(), LEFT_DB, RIGHT_DB);
            comparisonPage.waitForTableSelectionGrid();
            comparisonPage.setSelectedOnly(false);
            comparisonPage.waitForTableGridContains("Supplier");

            comparisonPage.waitForTableSelectionState("dbo-supplier", false, "dbo-product", false);
            comparisonPage.clickTableCheckbox("dbo-supplier");
            comparisonPage.pressKey("Space");
            comparisonPage.waitForTableSelectionState("dbo-supplier", true, "dbo-product", false);

            comparisonPage.pressKey("ArrowDown");
            comparisonPage.clickTableCheckbox("dbo-product");
            comparisonPage.waitForTableSelectionState("dbo-supplier", true, "dbo-product", true);

            comparisonPage.pressKey("ArrowUp");
            comparisonPage.clickTableCheckbox("dbo-supplier");
            comparisonPage.waitForTableSelectionState("dbo-supplier", false, "dbo-product", true);

            assertThat(comparisonPage.isCompareButtonEnabled()).isTrue();
        }
    }

    @Test
    void baselineDateTimePickerFiltersAndClearsCommandRows() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            final LoginPageObject loginPage = new LoginPageObject(page);
            final ComparisonPageObject comparisonPage = new ComparisonPageObject(page);
            loginPage.open("http://localhost:" + serverPort);
            loginPage.login(fixture.jdbcUrl(), fixture.username(), fixture.password(), LEFT_DB, RIGHT_DB);
            comparisonPage.waitForCommandSelectionGrid();

            comparisonPage.waitForCommandGridContains("supplier.Supplier#registerProduct");
            comparisonPage.waitForCommandGridContains("product.Product#changeStatus");

            comparisonPage.setCommandBaselineDateTime("9999-12-31T23:59");
            comparisonPage.waitForCommandGridNotContains("supplier.Supplier#registerProduct");
            comparisonPage.waitForCommandGridNotContains("product.Product#changeStatus");

            comparisonPage.clearCommandBaselineDateTime();
            comparisonPage.waitForCommandGridContains("supplier.Supplier#registerProduct");
            comparisonPage.waitForCommandGridContains("product.Product#changeStatus");
        }
    }

    @Test
    void commandContextMenuShowsCopyRowDetailsAction() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            final LoginPageObject loginPage = new LoginPageObject(page);
            final ComparisonPageObject comparisonPage = new ComparisonPageObject(page);
            loginPage.open("http://localhost:" + serverPort);
            loginPage.login(fixture.jdbcUrl(), fixture.username(), fixture.password(), LEFT_DB, RIGHT_DB);
            comparisonPage.waitForCommandSelectionGrid();

            comparisonPage.openCommandContextMenu(fixture.commandInteractionId());
            assertThat(comparisonPage.isCommandContextMenuItemVisible("Set baseline from selected command")).isTrue();
            assertThat(comparisonPage.isCommandContextMenuItemVisible("Copy row details")).isTrue();
        }
    }

    @Test
    void pressingEnterRunsCompareAfterCommandDrivenSelectionEnablesCompare() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            final LoginPageObject loginPage = new LoginPageObject(page);
            final ComparisonPageObject comparisonPage = new ComparisonPageObject(page);
            loginPage.open("http://localhost:" + serverPort);
            loginPage.login(fixture.jdbcUrl(), fixture.username(), fixture.password(), LEFT_DB, RIGHT_DB);
            comparisonPage.waitForCommandSelectionGrid();

            comparisonPage.clickCommandCheckbox(fixture.commandInteractionId());
            comparisonPage.waitForCompareButtonEnabled();

            comparisonPage.clickCompareButton();
            comparisonPage.waitForComparisonResultsTabs();
            assertThat(comparisonPage.comparisonResultTabCount()).isGreaterThan(0);
        }
    }

    @Test
    void showsMainUiHappyPathWithFooterProgressStatusBehavior() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
             Page page = browser.newPage()) {
            page.setViewportSize(1440, 900);
            final LoginPageObject loginPage = new LoginPageObject(page);
            final ComparisonPageObject comparisonPage = new ComparisonPageObject(page);
            loginPage.open("http://localhost:" + serverPort);
            assertThat(comparisonPage.isCompareButtonDisabled()).isTrue();
            loginPage.login(fixture.jdbcUrl(), fixture.username(), fixture.password(), LEFT_DB, RIGHT_DB);
            comparisonPage.waitForMainSelectionUi();

            assertThat(page.locator("[data-testid='connection-status-state']").count()).isZero();
            assertThat(page.locator("[data-testid='connection-status-summary']").count()).isZero();

            assertThat(page.locator("[data-testid='hamburger-menu']").count()).isEqualTo(1);
            final String footerText = page.locator("[data-testid='connection-details-footer']").innerText();
            assertThat(footerText).contains(LEFT_DB, RIGHT_DB);
            assertThat(footerText).doesNotContain(fixture.jdbcUrl(), fixture.password(), "SQL connectivity status");

            assertThat(page.locator("[data-testid='selected-table-feedback']").count()).isZero();
            assertThat(comparisonPage.isCompareButtonDisabled()).isTrue();
            assertThat(page.locator("[data-testid='clear-selections-button']").isDisabled()).isTrue();
            assertThat(page.locator("[data-testid='apply-table-filter']").count()).isZero();
            comparisonPage.waitForAccountMenuVisible();
            assertThat(page.locator("[data-testid='account-menu']").count()).isEqualTo(1);
            assertThat(page.locator("[data-testid='account-menu-label']").innerText()).contains(fixture.username());
            assertThat(page.locator("[data-testid='logout-button']").count()).isZero();

            assertThat(page.locator("[data-testid='command-selection-spacer']").count()).isEqualTo(1);

            final double memberFilterTop = comparisonPage.positionOf("[data-testid='command-filter-member-id']")[1];
            final double interactionFilterTop = comparisonPage.positionOf("[data-testid='command-filter-interaction-id']")[1];
            final double commandGridTop = comparisonPage.positionOf("[data-testid='command-selection-grid']")[1];
            final double compareTop = comparisonPage.positionOf("[data-testid='navigation-compare-action-bar']")[1];
            final double tableGridTop = comparisonPage.positionOf("[data-testid='table-selection-grid']")[1];
            assertThat(Math.abs(memberFilterTop - interactionFilterTop)).isLessThan(16.0);
            assertThat(commandGridTop).isLessThan(tableGridTop);
            assertThat(compareTop).isGreaterThan(tableGridTop);

            final String commandGridText = comparisonPage.commandGridText();
            assertThat(commandGridText).contains(
                    "Replay state",
                    "Member",
                    "Timestamp",
                    "Interaction",
                    fixture.commandInteractionId(),
                    fixture.secondCommandInteractionId());
            assertThat(commandGridText.indexOf("Replay state")).isLessThan(commandGridText.indexOf("Member"));
            assertThat(commandGridText.indexOf("Member")).isLessThan(commandGridText.indexOf("Timestamp"));
            assertThat(commandGridText.indexOf("Timestamp")).isLessThan(commandGridText.indexOf("Interaction"));

            final double[] footerMetrics = comparisonPage.footerStatusAlignmentMetrics();
            assertThat(footerMetrics[0]).isGreaterThan(footerMetrics[1]);

            assertThat(comparisonPage.isSelectedOnlyChecked()).isTrue();

            final String initialGridText = comparisonPage.tableGridText();
            assertThat(initialGridText).doesNotContain("Supplier", "Product", "PurchaseOrderWithoutBusinessKey", "SupplierMetadataExcluded");
            assertThat(initialGridText).doesNotContain("CommandLogEntry", "AuditTrailEntry", "LogicalTypeTableMapping");

            comparisonPage.setSelectedOnly(false);
            comparisonPage.waitForTableGridContains("Supplier");
            assertThat(page.locator("[data-testid='table-checkbox-dbo-purchaseorderwithoutbusinesskey']").getAttribute("disabled")).isNotNull();
            assertThat(page.locator("[data-testid='table-checkbox-dbo-purchaseorderwithoutbusinesskey']").getAttribute("title")).isNotBlank();
            assertThat(page.locator("[data-testid='table-checkbox-dbo-suppliermetadataexcluded']").getAttribute("disabled")).isNotNull();
            assertThat(page.locator("[data-testid='table-checkbox-dbo-suppliermetadataexcluded']").getAttribute("title")).contains("extended-property metadata");

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

            assertThat(comparisonPage.isCheckboxChecked("[data-testid='command-filter-replay-state-ok']")).isFalse();
            assertThat(comparisonPage.isCheckboxChecked("[data-testid='command-filter-replay-state-pending']")).isFalse();
            assertThat(comparisonPage.isCheckboxChecked("[data-testid='command-filter-replay-state-failed']")).isFalse();

            page.setViewportSize(1440, 900);
            page.waitForTimeout(200);

            comparisonPage.setCommandMemberFilter("supplier.Supplier");
            comparisonPage.waitForCommandGridContainsAndNotContains("supplier.Supplier#registerProduct", "product.Product#changeStatus");
            comparisonPage.setCommandInteractionFilter(fixture.commandInteractionId().substring(0, 8));
            comparisonPage.waitForCommandGridContains("11111111-1111-1111-1111-111111111111");

            comparisonPage.setCommandMemberFilter("");
            comparisonPage.setCommandInteractionFilter("");
            comparisonPage.click("[data-testid='command-filter-replay-state-pending']");
            comparisonPage.waitForCommandGridContainsAndNotContains("product.Product#changeStatus", "supplier.Supplier#registerProduct");
            comparisonPage.click("[data-testid='command-filter-replay-state-pending']");
            comparisonPage.click("[data-testid='command-filter-replay-state-ok']");
            comparisonPage.waitForCommandGridContainsAndNotContains("supplier.Supplier#registerProduct", "product.Product#changeStatus");
            comparisonPage.click("[data-testid='command-filter-replay-state-ok']");

            comparisonPage.clickCommandCheckbox(fixture.commandInteractionId());
            comparisonPage.waitForCheckboxState("[data-testid='table-checkbox-dbo-supplier']", true);
            comparisonPage.waitForCheckboxState("[data-testid='table-checkbox-dbo-product']", false);
            assertThat(page.locator("[data-testid='clear-selections-button']").isEnabled()).isTrue();

            comparisonPage.clickCommandCheckbox(fixture.secondCommandInteractionId());
            comparisonPage.waitForCheckboxState("[data-testid='table-checkbox-dbo-supplier']", true);
            comparisonPage.waitForCheckboxState("[data-testid='table-checkbox-dbo-product']", true);

            comparisonPage.click("[data-testid='clear-selections-button']");
            comparisonPage.waitForCheckboxState(comparisonPage.commandCheckboxSelector(fixture.commandInteractionId()), false);
            comparisonPage.waitForCheckboxState(comparisonPage.commandCheckboxSelector(fixture.secondCommandInteractionId()), false);
            assertThat(page.locator("[data-testid='clear-selections-button']").isDisabled()).isTrue();

            comparisonPage.setSelectedOnly(false);
            comparisonPage.setTableFilter(fixture.eligibleTable());
            comparisonPage.waitForTableGridContainsAndNotContains("Supplier", "PurchaseOrderWithoutBusinessKey");
            final String filteredText = comparisonPage.tableGridText();
            assertThat(filteredText).contains("Supplier");
            assertThat(filteredText).doesNotContain("PurchaseOrderWithoutBusinessKey");

            comparisonPage.setTableFilter("");
            comparisonPage.waitForTableGridContains("PurchaseOrderWithoutBusinessKey");
            comparisonPage.sortTableGridByTableColumn();
            page.waitForTimeout(250);
            final String sortedGridText = comparisonPage.tableGridText();
            assertThat(sortedGridText.indexOf("PurchaseOrderWithoutBusinessKey")).isLessThan(sortedGridText.indexOf("Supplier"));

            comparisonPage.selectAllEligibleTables();
            comparisonPage.waitForCompareButtonEnabled();
            assertThat(comparisonPage.isCompareButtonEnabled()).isTrue();

            comparisonPage.clickCompareButton();
            comparisonPage.waitForComparisonResultsTabs();
            comparisonPage.waitForComparisonCompleteMessage();
            final String successProgressClass = (String) page.evaluate(
                    "() => document.querySelector('[data-testid=\"comparison-progress-summary\"]')?.getAttribute('class') ?? ''");
            assertThat(successProgressClass).contains("comparison-progress-summary-success");
            assertThat(comparisonPage.isCheckboxChecked("[data-testid='comparison-differences-only-filter']")).isFalse();
            assertThat(page.locator("[data-testid='comparison-diff-columns-only-filter']").count()).isEqualTo(1);
            assertThat(page.locator("[data-testid^='comparison-result-tab-']").count()).isEqualTo(3);
            assertThat(page.locator("[data-testid='comparison-result-tab-dbo-supplier']").getAttribute("data-has-differences")).isEqualTo("true");
            assertThat(page.locator("[data-testid='comparison-result-tab-dbo-product']").getAttribute("data-has-differences")).isEqualTo("true");
            assertThat(page.locator("[data-testid='comparison-result-tab-dbo-customeraddress']").getAttribute("data-has-differences")).isEqualTo("false");

            comparisonPage.click("[data-testid='comparison-differences-only-filter']");
            comparisonPage.waitForComparisonResultTabCount(2);
            assertThat(page.locator("[data-testid='comparison-result-tab-dbo-customeraddress']").count()).isEqualTo(0);

            assertThat(page.locator("[data-testid^='comparison-grid-dbo-']").count()).isEqualTo(1);
            final String gridText = page.locator("[data-testid^='comparison-grid-dbo-']").first().innerText();
            assertThat(gridText).contains("Business Key", "Status", "name");
            assertThat(gridText).doesNotContain("L:", "R:");

            final Object resultsLayoutMetrics = page.evaluate("""
                    () => {
                      const scrollContainer = document.querySelector('[data-testid^="comparison-grid-scroll-container-"]');
                      const footer = document.querySelector('[data-testid="connection-details-footer"]');
                      if (!scrollContainer || !footer) return [-1, -1, -1];
                      const scrollRect = scrollContainer.getBoundingClientRect();
                      const footerRect = footer.getBoundingClientRect();
                      return [scrollRect.bottom, footerRect.top, scrollRect.height];
                    }
                    """);
            final List<?> metrics = (List<?>) resultsLayoutMetrics;
            final double scrollBottom = ((Number) metrics.get(0)).doubleValue();
            final double footerTop = ((Number) metrics.get(1)).doubleValue();
            final double scrollHeight = ((Number) metrics.get(2)).doubleValue();
            assertThat(scrollBottom).isLessThanOrEqualTo(footerTop);
            assertThat(scrollHeight).isGreaterThan(220.0);
            assertThat(page.locator("[data-testid='comparison-table-filter']").count()).isEqualTo(1);
            assertThat(page.locator("[data-testid='comparison-differences-only-filter']").count()).isEqualTo(1);
            assertThat(page.locator("[data-testid='comparison-diff-columns-only-filter']").count()).isEqualTo(1);
            assertThat(page.locator("[data-testid='download-format-select']").count()).isEqualTo(1);
            assertThat(page.locator("[data-testid='download-action']").count()).isEqualTo(1);

            comparisonPage.setTableFilter("Supplier");
            comparisonPage.waitForProgressMessageCleared();
            final String clearedProgressClass = (String) page.evaluate(
                    "() => document.querySelector('[data-testid=\"comparison-progress-summary\"]')?.getAttribute('class') ?? ''");
            assertThat(clearedProgressClass)
                    .doesNotContain("comparison-progress-summary-success", "comparison-progress-summary-failure");

            comparisonPage.clickCompareButton();
            comparisonPage.waitForComparisonCompleteMessage();
            comparisonPage.click("[data-testid='clear-selections-button']");
            comparisonPage.waitForProgressMessageCleared();

            page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath("webapp-selected.png")).setFullPage(true));
        }
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
}
