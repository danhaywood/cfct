## 1. Footer status model simplification

- [ ] 1.1 Remove `connectionStatusState` and `connectionStatusSummary` fields, rendering, and update paths from `cfct-webapp` `MainView`.
- [ ] 1.2 Keep `comparisonProgressSummary` as the single footer status output and preserve running-progress message updates.
- [ ] 1.3 Add deterministic neutral/success/failure style hooks for `comparisonProgressSummary` terminal and non-terminal states.

## 2. Stale status reset triggers

- [ ] 2.1 Add a shared `MainView` helper that clears footer comparison status text and removes terminal style hooks.
- [ ] 2.2 Invoke status reset helper when command-section Clear is activated.
- [ ] 2.3 Invoke status reset helper when command-grid selection or filter parameters change.
- [ ] 2.4 Invoke status reset helper when business-table selection or filter parameters change.

## 3. Tests and verification

- [ ] 3.1 Update unit/UI tests to assert absence of connection status state/summary labels in the footer.
- [ ] 3.2 Update Playwright tests to assert success/failure footer background cues and stale-status reset behavior.
- [ ] 3.3 Refresh affected Playwright screenshot baselines and verify CI/headless execution path remains valid.
