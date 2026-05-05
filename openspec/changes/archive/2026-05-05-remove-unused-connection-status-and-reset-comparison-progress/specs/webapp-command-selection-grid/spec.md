## ADDED Requirements

### Requirement: Command-grid selection parameter changes reset comparison status report
The webapp SHALL clear any previously rendered comparison progress status report when command-grid selection parameters change.
Command-grid selection parameters SHALL include command-row selection changes and command-grid filter changes.
The webapp SHALL preserve command and table selection behavior while clearing only stale comparison status reporting.

#### Scenario: Command selection change clears prior comparison status report
- **WHEN** a prior comparison status report is visible in the footer
- **AND** the user selects or deselects one or more command rows
- **THEN** the prior comparison status report is cleared

#### Scenario: Command filter change clears prior comparison status report
- **WHEN** a prior comparison status report is visible in the footer
- **AND** the user changes member, interactionId, or replay-state filter parameters
- **THEN** the prior comparison status report is cleared

## MODIFIED Requirements

### Requirement: Clear control resets command and business selections
The webapp SHALL render a Clear control below the command selection grid.
The Clear control SHALL clear selected rows in the command grid and the business table grid.
The Clear control SHALL clear any previously rendered comparison progress status report.
The Clear control SHALL be disabled when no rows are selected in either grid.

#### Scenario: Clear button appears below command grid
- **WHEN** the drawer is rendered
- **THEN** a clear selection control is visible below the command selection grid

#### Scenario: Clear button clears command and table selections
- **WHEN** one or more command rows or business table rows are selected
- **AND** the user clicks Clear
- **THEN** command selection becomes empty
- **AND** business table selection becomes empty
- **AND** any prior comparison status report is cleared

#### Scenario: Clear button disablement reflects empty state
- **WHEN** no command rows and no business table rows are selected
- **THEN** the Clear control is disabled
- **AND** compare action remains disabled until new eligible table selections exist
