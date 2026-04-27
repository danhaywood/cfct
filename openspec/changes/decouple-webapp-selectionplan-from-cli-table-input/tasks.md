## 1. SelectionPlan abstraction

- [ ] 1.1 Add a `SelectionPlan` interface in `sqlcomparer-webapp` that resolves a deterministic `List<TableRef>`.
- [ ] 1.2 Add an explicit concrete selection-plan implementation that stores and returns concrete `TableRef` values.
- [ ] 1.3 Add unit tests for explicit selection-plan behavior, including deterministic output ordering.

## 2. Webapp execution and configuration refactor

- [ ] 2.1 Refactor webapp comparison preparation flow to consume `SelectionPlan` output instead of CLI-style table-selection config fields.
- [ ] 2.2 Update `WebappComparisonProperties` so shared execution settings remain aligned with CLI concepts while table-selection fields are removed or clearly migrated.
- [ ] 2.3 Remove legacy table-source conflict validation that depended on config-level `tables` versus `tables-file`.

## 3. Documentation and contract alignment

- [ ] 3.1 Update README webapp section to state that table selection is strategy-driven via `SelectionPlan` and not bound to CLI table flags.
- [ ] 3.2 Update code-level mapping documentation to clarify shared execution config parity and channel-specific selection strategy.
- [ ] 3.3 Add migration notes for any removed or deprecated webapp table-selection configuration keys.

## 4. Validation

- [ ] 4.1 Run webapp module tests covering startup and configuration binding after selection-plan refactor.
- [ ] 4.2 Run reactor tests for affected modules to verify no regressions in CLI behavior.
- [ ] 4.3 Verify the webapp still starts successfully in local dev mode after the selection-plan integration.
