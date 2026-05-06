## ADDED Requirements

### Requirement: Compare run surfaces row-level completion cues and live counter
The webapp SHALL show per-table comparison completion cues in the manual business table grid while a compare run is active.
The webapp SHALL apply a completion background cue to a business table row as soon as that table comparison completes.
The webapp SHALL display a live completed counter adjacent to the Compare action using `completed of total` format.
The webapp SHALL update the completed counter each time a table completion or failure event is received.
The webapp SHALL clear row completion cues and hide the completed counter once the active run ends and a new selection workflow begins.

#### Scenario: Completed table row is highlighted during active run
- **WHEN** a compare run is active and a selected table finishes comparison
- **THEN** the corresponding business table row renders with the configured completion background cue

#### Scenario: Completed counter updates as tables finish
- **WHEN** a compare run starts with five selected tables
- **THEN** the compare-adjacent progress label starts at `0 of 5`
- **AND** advances to `1 of 5`, `2 of 5`, and so on as tables complete

#### Scenario: Counter and cues clear before next selection workflow
- **WHEN** a prior compare run has displayed row completion cues and a completed counter
- **AND** the user begins a new selection workflow
- **THEN** the webapp clears all prior row completion cues and hides the completed counter before the next run

## MODIFIED Requirements

### Requirement: Manual-table selection parameter changes reset comparison status report
The webapp SHALL clear any previously rendered comparison progress status report when manual-table selection parameters change.
Manual-table selection parameters SHALL include business-table row selection changes and business-table filter changes.
The webapp SHALL clear any row-level comparison completion cues when manual-table selection parameters change.
The webapp SHALL hide any compare-adjacent completed counter when manual-table selection parameters change.
The webapp SHALL preserve underlying business-table selection semantics while clearing only stale comparison status reporting and progress cues.

#### Scenario: Manual table selection change clears prior comparison status report
- **WHEN** a prior comparison status report is visible in the footer
- **AND** the user selects or deselects one or more eligible business table rows
- **THEN** the prior comparison status report is cleared
- **AND** prior row completion cues are cleared
- **AND** any compare-adjacent completed counter is hidden

#### Scenario: Manual table filter change clears prior comparison status report
- **WHEN** a prior comparison status report is visible in the footer
- **AND** the user changes one or more business-table filter parameters
- **THEN** the prior comparison status report is cleared
- **AND** prior row completion cues are cleared
- **AND** any compare-adjacent completed counter is hidden
