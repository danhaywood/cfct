## 1. Footer/status bar layout and content

- [x] 1.1 Update footer rendering to show only left/right database names and remove displayed JDBC URL.
- [x] 1.2 Add explicit horizontal padding and consistent inter-label spacing in the footer status bar.
- [x] 1.3 Add bottom clearance behavior so footer does not overlap compare-action controls in constrained viewports.
- [x] 1.4 Add or update UI tests to assert footer content excludes JDBC URL and preserves spacing/visibility expectations.

## 2. Account menu username label

- [x] 2.1 Update account-menu top-level label logic to include authenticated username when available with deterministic fallback.
- [x] 2.2 Add or update unit and/or Playwright tests verifying username appears in top-right account menu label for authenticated sessions.

## 3. Validation and QA

- [x] 3.1 Run relevant unit and browser-level test suites for main layout, footer status bar, and account-menu behavior.
- [x] 3.2 Perform manual QA on small and normal viewport sizes to verify no compare-button/footer overlap and readable footer spacing.