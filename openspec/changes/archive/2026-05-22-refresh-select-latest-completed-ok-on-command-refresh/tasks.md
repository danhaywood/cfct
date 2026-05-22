## 1. Refresh auto-selection behavior

- [x] 1.1 Update command refresh flow to clear prior selection, then auto-select the newest `OK` command row when present.
- [x] 1.2 Keep `focusedCommandInteractionId` on the auto-selected row and ensure command-driven table selection recomputes once.
- [x] 1.3 Preserve empty-selection behavior when no refreshed `OK` row exists.

## 2. Test coverage

- [x] 2.1 Update `MainViewTest` refresh tests to assert newest `OK` row becomes selected after refresh.
- [x] 2.2 Add or update tests asserting focus stays on the auto-selected row.
- [x] 2.3 Run targeted `cfct-webapp` command-selection tests.
