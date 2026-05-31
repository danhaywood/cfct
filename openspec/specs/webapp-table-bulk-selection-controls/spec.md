# webapp-table-bulk-selection-controls Specification

## Purpose
TBD - created by archiving change multi-table-selection-and-select-all. Update Purpose after archive.
## Requirements
### Requirement: Manual table selection provides select-all control for selectable rows
The manual table-selection section SHALL provide a `Select all` checkbox control.
The `Select all` checkbox SHALL select every selectable table row when checked.
The `Select all` checkbox SHALL NOT select disabled or ineligible table rows.
Unchecking `Select all` SHALL clear selection for selectable rows while leaving disabled rows unchanged.
The `Select all` checkbox state SHALL reflect selectable-row selection state and SHALL support an indeterminate state when only a subset is selected.

#### Scenario: User checks select-all with mixed eligibility rows
- **WHEN** the grid contains selectable and disabled rows and the user checks `Select all`
- **THEN** all selectable rows become selected
- **AND** disabled rows remain unselected

#### Scenario: User unchecks select-all after bulk selection
- **WHEN** selectable rows are selected and the user unchecks `Select all`
- **THEN** selection is cleared for selectable rows
- **AND** disabled rows remain unchanged

#### Scenario: Select-all shows indeterminate for partial selectable selection
- **WHEN** only some selectable rows are selected
- **THEN** the `Select all` checkbox displays an indeterminate state
- **AND** checking it selects the remaining selectable rows

