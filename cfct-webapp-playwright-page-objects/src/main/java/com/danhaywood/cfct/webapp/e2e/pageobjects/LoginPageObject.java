package com.danhaywood.cfct.webapp.e2e.pageobjects;

import com.microsoft.playwright.Page;

import java.util.List;

public class LoginPageObject {

    private final Page page;

    public LoginPageObject(final Page page) {
        this.page = page;
    }

    public void open(final String baseUrl) {
        page.navigate(baseUrl + "/");
        page.waitForSelector("[data-testid='login-submit']");
    }

    public void fillLoginForm(
            final String jdbcUrl,
            final String username,
            final String password,
            final String leftDatabase,
            final String rightDatabase) {
        setLoginField("login-jdbc-url", jdbcUrl);
        setLoginField("login-username", username);
        setLoginField("login-password", password);
        setLoginField("login-left-database", leftDatabase);
        setLoginField("login-right-database", rightDatabase);
    }

    public void submit() {
        page.click("[data-testid='login-submit']");
    }

    public void login(
            final String jdbcUrl,
            final String username,
            final String password,
            final String leftDatabase,
            final String rightDatabase) {
        fillLoginForm(jdbcUrl, username, password, leftDatabase, rightDatabase);
        submit();
    }

    public String loginErrorText() {
        page.waitForSelector("[data-testid='login-error']");
        return page.locator("[data-testid='login-error']").innerText();
    }

    private void setLoginField(final String testId, final String value) {
        page.evaluate("([id, val]) => { const host = document.querySelector(`[data-testid='${id}']`); if (!host) return; host.value = val; host.dispatchEvent(new Event('input', { bubbles: true, composed: true })); host.dispatchEvent(new Event('change', { bubbles: true, composed: true })); }", List.of(testId, value));
    }
}
