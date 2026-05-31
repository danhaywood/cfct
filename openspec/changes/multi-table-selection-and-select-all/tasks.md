## 1. Selection interaction foundation

- [ ] 1.1 Identify the manual table selection view and state-management classes that currently handle row selection, keyboard space toggling, and row eligibility.
- [ ] 1.2 Add a shared selection update path that is used by checkbox clicks, keyboard actions, shift interactions, and bulk select actions.
- [ ] 1.3 Introduce and maintain a selection anchor model needed for deterministic `Shift+Click` and `Shift+Space` range behavior.

## 2. Shift multi-select support

- [ ] 2.1 Implement `Shift+Click` handling in the manual table grid to perform additive range selection across eligible rows.
- [ ] 2.2 Implement `Shift+Space` keyboard handling on the focused manual table row to perform additive range selection.
- [ ] 2.3 Ensure shift-based selection explicitly skips disabled or ineligible rows while still applying selection to eligible rows in scope.

## 3. Select-all control support

- [ ] 3.1 Add a `Select all` checkbox to the manual table selection section and bind it to table selection state.
- [ ] 3.2 Implement select-all checked behavior to select all selectable rows and leave disabled rows unselected.
- [ ] 3.3 Implement select-all unchecked behavior to clear selectable-row selection and derive tri-state checkbox status for partial selection.

## 4. Verification and regression coverage

- [ ] 4.1 Add or update UI tests for `Shift+Click` multi-selection, including ranges containing disabled rows.
- [ ] 4.2 Add or update UI tests for `Shift+Space` keyboard multi-selection and anchor behavior.
- [ ] 4.3 Add or update UI tests for `Select all` checked, unchecked, and indeterminate states with disabled rows present.
- [ ] 4.4 Run relevant webapp test suites and verify existing manual-table selection behavior remains stable outside the new interactions.
