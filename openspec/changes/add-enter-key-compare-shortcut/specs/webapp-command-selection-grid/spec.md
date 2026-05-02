## MODIFIED Requirements

### Requirement: Command selection drives business table auto-selection
The webapp SHALL evaluate touched business tables for the currently selected commands.
The webapp SHALL update business table selections to include the resolved touched tables.
The webapp SHALL use union semantics across all selected commands.
The webapp SHALL allow users to press Enter to execute compare immediately after command-driven table selection has enabled compare.

#### Scenario: Selecting one command auto-selects touched business tables
- **WHEN** a user selects a command row in the command grid
- **THEN** the webapp resolves touched tables for that command interaction
- **AND** matching business table rows in the manual table grid are selected

#### Scenario: Selecting multiple commands unions touched business tables
- **WHEN** a user selects multiple command rows
- **THEN** the webapp resolves touched tables for each selected interaction
- **AND** the manual table grid selection contains the union of matching business tables

#### Scenario: Deselecting commands updates downstream table selection
- **WHEN** a previously selected command row is deselected
- **THEN** the webapp recomputes touched-table union for remaining selected commands
- **AND** the manual table grid reflects the recomputed union

#### Scenario: Clearing command selection clears command-driven table selection
- **WHEN** the user clears all command selections
- **THEN** no command-driven table selection remains active in the manual table grid

#### Scenario: Enter executes compare after command-driven selection enables compare
- **WHEN** command-driven table selection has produced at least one eligible selected table and the user presses Enter
- **THEN** compare execution starts using the currently selected eligible tables
