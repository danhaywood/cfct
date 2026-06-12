## 1. Command Catalog Pending Count

- [ ] 1.1 Add a focused predicate or helper for identifying pending background command catalog entries using case-insensitive `executeIn == BACKGROUND` and `replayState == PENDING` checks.
- [ ] 1.2 Compute the pending background command count from the same command catalog snapshot used by `AutomationComparisonService` during refresh.
- [ ] 1.3 Preserve the existing newest-successful-command selection and touched-table resolution behavior.

## 2. Automation JSON Metadata

- [ ] 2.1 Extend automation result metadata to carry the pending background command count alongside existing command metadata.
- [ ] 2.2 Extend the automation JSON enrichment path to add `backgroundCommands.pending` as a numeric field in successful populated comparison responses.
- [ ] 2.3 Ensure successful empty comparison responses also include `backgroundCommands.pending`.
- [ ] 2.4 Keep automation controller headers, filename behavior, conflict handling, and error payloads unchanged.

## 3. Tests and Fixtures

- [ ] 3.1 Update `AutomationComparisonServiceTest` to assert `backgroundCommands.pending` when pending background commands exist.
- [ ] 3.2 Add or update tests to assert non-background, successful, and failed command catalog entries are not counted as pending background commands.
- [ ] 3.3 Update empty-comparison automation tests to assert `backgroundCommands.pending` is present.
- [ ] 3.4 Update `AutomationControllerTest` to assert successful downloads include `backgroundCommands.pending` without changing failure responses.

## 4. Verification

- [ ] 4.1 Run the relevant webapp automation test suite.
- [ ] 4.2 Run the broader Maven test set needed to validate JSON comparison output and automation endpoint behavior.
- [ ] 4.3 Run `openspec status --change "add-background-command-pending-metadata"` and confirm the change is apply-ready.
