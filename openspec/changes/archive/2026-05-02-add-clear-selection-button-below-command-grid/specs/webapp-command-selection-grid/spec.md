## ADDED Requirements

### Requirement: Clear control resets command and business selections
The webapp SHALL render a Clear control below the command selection grid.
The Clear control SHALL clear selected rows in the command grid and the business table grid.
The Clear control SHALL be disabled when no rows are selected in either grid.

#### Scenario: Clear button appears below command grid
- **WHEN** the drawer is rendered
- **THEN** a clear selection control is visible below the command selection grid

#### Scenario: Clear button clears command and table selections
- **WHEN** one or more command rows or business table rows are selected
- **AND** the user clicks Clear
- **THEN** command selection becomes empty
- **AND** business table selection becomes empty

#### Scenario: Clear button disablement reflects empty state
- **WHEN** no command rows and no business table rows are selected
- **THEN** the Clear control is disabled
- **AND** compare action remains disabled until new eligible table selections exist
