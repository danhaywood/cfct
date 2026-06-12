## Why

Automation currently treats a newest successful command with no eligible touched business tables as a failed comparison, returning a `500` even when the action was safely non-changing.
The JSON report also only exposes the table-result list, which makes it hard for automation clients to distinguish no differences from no tables compared.

## What Changes

- Extend JSON comparison output to expose `differingTables` for table results with differences and `comparedTables` for every table actually compared.
- **BREAKING**: Rename the top-level JSON `tables` result array to `differingTables`.
- Preserve `hasDifferences` as the top-level summary flag.
- Represent a safe non-changing automation action with no eligible business tables as a successful empty JSON report: `hasDifferences: false`, `differingTables: []`, and `comparedTables: []`.
- Update automation refresh/download behavior so no eligible touched business tables is not treated as an execution error.
- Update tests, approvals, and documentation for the new JSON shape.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `json-comparison-file`: Change the deterministic JSON output contract to separate differing table details from the complete list of compared tables, including empty comparisons.
- `webapp-automation-rest-api`: Change automation download behavior so commands with no eligible touched business tables return a successful empty comparison JSON rather than a failure response.

## Impact

- Affects JSON report rendering in the implementation module and consumers that parse the old top-level `tables` field.
- Affects automation comparison service handling of empty command-driven selections.
- Affects JSON renderer tests, integration approval files, webapp automation tests, and README automation examples.
- Does not require changes to UI comparison rendering or the underlying row/table comparison algorithms.
