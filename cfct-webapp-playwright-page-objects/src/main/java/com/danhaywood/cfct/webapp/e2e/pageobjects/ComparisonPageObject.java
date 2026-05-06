package com.danhaywood.cfct.webapp.e2e.pageobjects;

import com.microsoft.playwright.Page;

import java.util.List;

public class ComparisonPageObject {

    private final Page page;

    public ComparisonPageObject(final Page page) {
        this.page = page;
    }

    public void waitForMainSelectionUi() {
        page.waitForSelector("[data-testid='comparison-progress-summary']");
        page.waitForSelector("[data-testid='command-selection-grid']");
        page.waitForSelector("[data-testid='table-selection-grid']");
    }

    public String commandCheckboxSelector(final String interactionId) {
        return "[data-testid='command-checkbox-" + interactionId.toLowerCase() + "']";
    }

    public String tableCheckboxSelector(final String qualifiedTableNameLower) {
        return "[data-testid='table-checkbox-" + qualifiedTableNameLower + "']";
    }

    public void waitForCheckboxState(final String selector, final boolean checked) {
        page.waitForSelector(selector);
        page.waitForFunction("([s, checked]) => { const el = document.querySelector(s); return !!el && !!el.checked === checked; }", List.of(selector, checked));
    }

    public void waitForTwoCheckboxStates(
            final String firstSelector,
            final boolean firstChecked,
            final String secondSelector,
            final boolean secondChecked) {
        page.waitForSelector(firstSelector);
        page.waitForSelector(secondSelector);
        page.waitForFunction("([first, firstChecked, second, secondChecked]) => { const a = document.querySelector(first); const b = document.querySelector(second); return !!a && !!b && !!a.checked === firstChecked && !!b.checked === secondChecked; }",
                List.of(firstSelector, firstChecked, secondSelector, secondChecked));
    }

    public void pressKey(final String key) {
        page.keyboard().press(key);
    }

    public void click(final String selector) {
        page.locator(selector).click();
    }

    public void setSelectedOnly(final boolean checked) {
        if (isSelectedOnlyChecked() != checked) {
            page.locator("[data-testid='selected-only-checkbox']").click();
        }
        page.waitForFunction("(checked) => { const host = document.querySelector('[data-testid=\"selected-only-checkbox\"]'); return !!host && !!host.checked === checked; }", checked);
    }

    public boolean isSelectedOnlyChecked() {
        final Object evaluated = page.evaluate("() => !!document.querySelector('[data-testid=\"selected-only-checkbox\"]')?.checked");
        return Boolean.TRUE.equals(evaluated);
    }

    public boolean isCheckboxChecked(final String selector) {
        final Object evaluated = page.evaluate("(selector) => { const host = document.querySelector(selector); return host ? !!host.checked : false; }", selector);
        return Boolean.TRUE.equals(evaluated);
    }

    public void setTableFilter(final String value) {
        page.locator("[data-testid='table-filter-table'] input").fill(value);
        page.keyboard().press("Tab");
    }

    public void setCommandMemberFilter(final String value) {
        page.locator("[data-testid='command-filter-member-id'] input").fill(value);
        page.keyboard().press("Tab");
    }

    public void setCommandInteractionFilter(final String value) {
        page.locator("[data-testid='command-filter-interaction-id'] input").fill(value);
        page.keyboard().press("Tab");
    }

    public void selectAllEligibleTables() {
        page.evaluate("() => { document.querySelectorAll('[data-testid^=\"table-checkbox-\"]').forEach((host) => { if (host.hasAttribute('disabled')) return; host.checked = true; host.dispatchEvent(new CustomEvent('checked-changed', { detail: { value: true }, bubbles: true, composed: true })); host.dispatchEvent(new Event('change', { bubbles: true, composed: true })); }); }");
    }

    public void waitForComparisonCompleteMessage() {
        page.waitForFunction("() => document.querySelector('[data-testid=\"comparison-progress-summary\"]').innerText.includes('Comparison complete.')");
    }

    public void waitForProgressMessageCleared() {
        page.waitForFunction("() => document.querySelector('[data-testid=\"comparison-progress-summary\"]').innerText.trim() === ''");
    }

    public double[] positionOf(final String selector) {
        final Object evaluated = page.evaluate("(selector) => { const r = document.querySelector(selector)?.getBoundingClientRect(); return r ? [r.left, r.top, r.right, r.bottom] : [-1, -1, -1, -1]; }", selector);
        return toDoubleArray((List<?>) evaluated);
    }

    public double[] footerStatusAlignmentMetrics() {
        final Object evaluated = page.evaluate("() => { const footer = document.querySelector('[data-testid=\"connection-details-footer\"]'); const panel = document.querySelector('[data-testid=\"connection-status-panel\"]'); if (!footer || !panel) return [-1, -1]; const fr = footer.getBoundingClientRect(); const pr = panel.getBoundingClientRect(); return [pr.right, fr.left + (fr.width / 2)]; }");
        return toDoubleArray((List<?>) evaluated);
    }

    private double[] toDoubleArray(final List<?> values) {
        final double[] array = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = ((Number) values.get(i)).doubleValue();
        }
        return array;
    }
}
