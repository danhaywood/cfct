## 1. Comparison-stage vertical layout expansion

- [ ] 1.1 Identify and update the right-side comparison-stage container chain so active result content can inherit full available height.
- [ ] 1.2 Ensure active result-tab content allocates remaining vertical space to the result grid region.
- [ ] 1.3 Keep results controls and tab headers at stable height while expanding only the grid viewport area.

## 2. Footer-safe bounds enforcement

- [ ] 2.1 Reserve deterministic bottom clearance for the fixed footer in comparison-stage layout sizing.
- [ ] 2.2 Ensure expanded result grids never overlap or hide beneath the footer at supported viewport sizes.
- [ ] 2.3 Verify scrolling remains within the result-grid area and does not require footer-overlapping page scroll.

## 3. Verification

- [ ] 3.1 Add or update unit/component tests for comparison-stage height propagation and footer non-overlap.
- [ ] 3.2 Add or update Playwright coverage to assert the result grid extends deeper while remaining above the footer.
- [ ] 3.3 Run targeted webapp test suites to verify no regression in comparison tabs, filters, and result-grid scrolling.
