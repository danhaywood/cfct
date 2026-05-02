## 1. Command-to-table selection orchestration

- [x] 1.1 Identify command selection change hook in webapp state flow and invoke touched-table resolution for selected interactions.
- [x] 1.2 Add orchestration logic to compute union of touched tables across selected commands.
- [x] 1.3 Apply resolved table refs to manual table selection state as programmatic selections.
- [x] 1.4 Recompute and update programmatic selections when commands are deselected or cleared.
- [x] 1.5 Keep manual filter, sort, and compare-readiness behavior stable after command-driven updates.

## 2. Tests

- [x] 2.1 Add or update unit tests for selection state and orchestration service to verify command-driven table selection semantics.
- [x] 2.2 Add or update UI tests to verify selecting commands auto-selects corresponding business tables.
- [x] 2.3 Add or update UI tests to verify multi-command union and clear/deselect behavior.
- [x] 2.4 Run relevant webapp tests and confirm behavior remains stable.
