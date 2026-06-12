## 1. JSON Report Shape

- [x] 1.1 Update `JsonMultiTableComparisonReportRenderer` so the top-level JSON contains `hasDifferences`, `differingTables`, and `comparedTables`.
- [x] 1.2 Move the existing detailed table result payload from top-level `tables` to `differingTables`.
- [x] 1.3 Filter `differingTables` so it includes only table results whose summary has differences.
- [x] 1.4 Add `comparedTables` as a deterministic table identity list for every compared table in comparison result order.
- [x] 1.5 Ensure an empty `MultiTableComparisonResult` renders as `hasDifferences: false`, `differingTables: []`, and `comparedTables: []`.

## 2. Automation Empty-Selection Behavior

- [x] 2.1 Update `AutomationComparisonService` so a newest successful command with no eligible touched business tables returns a successful empty JSON comparison result instead of throwing an error.
- [x] 2.2 Preserve existing error behavior when no newest successful command can be resolved.
- [x] 2.3 Preserve existing comparison execution behavior when one or more eligible business tables are resolved.
- [x] 2.4 Preserve the existing automation download status, content type, attachment filename, and concurrency behavior.

## 3. Tests and Approvals

- [x] 3.1 Update `JsonMultiTableComparisonReportRendererTest` for `differingTables` and `comparedTables`.
- [x] 3.2 Add JSON renderer coverage for clean compared tables being omitted from `differingTables` while present in `comparedTables`.
- [x] 3.3 Add JSON renderer coverage for empty comparison output.
- [x] 3.4 Update integration approval files or approval assertions affected by the JSON shape.
- [x] 3.5 Update `AutomationComparisonServiceTest` and `AutomationControllerTest` for successful empty JSON when no eligible touched business tables resolve.

## 4. Documentation and Validation

- [x] 4.1 Update README JSON/automation examples to describe `differingTables` and `comparedTables`.
- [x] 4.2 Remove documentation that implies no eligible business tables is an automation failure.
- [x] 4.3 Run the affected implementation and webapp tests and fix regressions.
- [x] 4.4 Run the relevant project validation command or module test suite before marking the change complete.
