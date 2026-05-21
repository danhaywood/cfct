## 1. Compare enablement lifecycle

- [ ] 1.1 Locate current `Compare` enable/disable state logic and identify post-run terminal-state handling.
- [ ] 1.2 Update logic so `Compare` is disabled only while a run is active or when no eligible tables are selected.
- [ ] 1.3 Ensure compare completion triggers immediate re-evaluation and re-enables `Compare` when eligible selections remain.

## 2. Compare-row spacing polish

- [ ] 2.1 Identify the compare-action row component/style where completed counter and `Compare` button are laid out.
- [ ] 2.2 Add explicit horizontal spacing between counter label and button using theme-consistent spacing tokens.
- [ ] 2.3 Verify no visual overlap across supported viewport sizes and drawer widths.

## 3. Regression coverage

- [ ] 3.1 Add or update tests asserting `Compare` disables during active run and re-enables after completion with eligible selections.
- [ ] 3.2 Add or update UI snapshot/assertion coverage for visible spacing between completed counter and `Compare` button.
- [ ] 3.3 Run relevant webapp and Playwright suites for compare action lifecycle and drawer layout behavior.
