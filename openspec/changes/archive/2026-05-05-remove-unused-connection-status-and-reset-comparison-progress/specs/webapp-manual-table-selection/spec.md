## ADDED Requirements

### Requirement: Manual-table selection parameter changes reset comparison status report
The webapp SHALL clear any previously rendered comparison progress status report when manual-table selection parameters change.
Manual-table selection parameters SHALL include business-table row selection changes and business-table filter changes.
The webapp SHALL preserve underlying business-table selection semantics while clearing only stale comparison status reporting.

#### Scenario: Manual table selection change clears prior comparison status report
- **WHEN** a prior comparison status report is visible in the footer
- **AND** the user selects or deselects one or more eligible business table rows
- **THEN** the prior comparison status report is cleared

#### Scenario: Manual table filter change clears prior comparison status report
- **WHEN** a prior comparison status report is visible in the footer
- **AND** the user changes one or more business-table filter parameters
- **THEN** the prior comparison status report is cleared
