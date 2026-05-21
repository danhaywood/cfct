## 1. Command context-menu enhancement

- [ ] 1.1 Locate command-grid context-menu construction and existing row actions.
- [ ] 1.2 Add a new context-menu action to copy current row details to clipboard.
- [ ] 1.3 Include member and interactionId in deterministic key/value clipboard text format.

## 2. UX and reliability

- [ ] 2.1 Preserve existing context-menu actions (including set baseline) and verify no behavioral regressions.
- [ ] 2.2 Add graceful failure handling or user feedback path for clipboard write failures.

## 3. Regression coverage

- [ ] 3.1 Add or update UI/component tests asserting copy action presence and payload content mapping.
- [ ] 3.2 Add or update browser-level tests for copy action visibility and interaction path.
- [ ] 3.3 Run targeted webapp tests covering command-grid context-menu features.
