## 1. Post-Login Focus Management

- [ ] 1.1 Update login-success flow to place focus on the command selection grid after modal close.
- [ ] 1.2 Add deterministic selectors or hooks needed to assert focus target reliably.

## 2. Keyboard Interaction for Command Grid

- [ ] 2.1 Implement Space-key handling to toggle selection for focused command row.
- [ ] 2.2 Ensure Up/Down and Left/Right arrow behavior remains useful and consistent with grid navigation semantics.
- [ ] 2.3 Ensure keyboard toggles reuse existing command-selection state update path.

## 3. Tests

- [ ] 3.1 Add or update unit tests for post-login focus placement on command grid.
- [ ] 3.2 Add or update tests for Space toggle behavior on focused command rows.
- [ ] 3.3 Add or update tests for arrow-key navigation behavior expectations.

## 4. Verification

- [ ] 4.1 Run relevant webapp unit and browser-level tests for keyboard interaction flow.
- [ ] 4.2 Update README or user-facing notes for keyboard command-grid interaction improvements.
