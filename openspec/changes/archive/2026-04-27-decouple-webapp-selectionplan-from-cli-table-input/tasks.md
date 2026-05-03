## 1. SelectionPlan abstraction

- [x] 1.1 Add a `SelectionPlan` interface in `cfct-webapp` that resolves a deterministic `List<TableRef>`.
- [x] 1.2 Add an explicit concrete selection-plan implementation that stores and returns concrete `TableRef` values.
- [x] 1.3 Add unit tests for explicit selection-plan behavior, including deterministic output ordering.

## 2. Webapp execution and configuration refactor

- [x] 2.1 Refactor webapp comparison preparation flow to consume `SelectionPlan` output instead of CLI-style table-selection config fields.
- [x] 2.2 Update `WebappComparisonProperties` so shared execution settings remain aligned with CLI concepts while table-selection fields are removed or clearly migrated.
- [x] 2.3 Remove legacy table-source conflict validation that depended on config-level `tables` versus `tables-file`.

## 3. Documentation and contract alignment

- [x] 3.1 Update README webapp section to state that table selection is strategy-driven via `SelectionPlan` and not bound to CLI table flags.
- [x] 3.2 Update code-level mapping documentation to clarify shared execution config parity and channel-specific selection strategy.
- [x] 3.3 Add migration notes for any removed or deprecated webapp table-selection configuration keys.

## 4. Validation

- [x] 4.1 Run webapp module tests covering startup and configuration binding after selection-plan refactor.
- [x] 4.2 Run reactor tests for affected modules to verify no regressions in CLI behavior.
- [x] 4.3 Verify the webapp still starts successfully in local dev mode after the selection-plan integration.
