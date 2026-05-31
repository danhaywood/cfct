## ADDED Requirements

### Requirement: Manual table selection supports shift-based multi-select interactions
The manual table-selection grid SHALL support additive multi-selection through `Shift+Click` on eligible rows.
The manual table-selection grid SHALL support additive multi-selection through `Shift+Space` when focus is on an eligible row.
Shift-based multi-selection SHALL use the same eligibility rules as single-row selection.
Shift-based multi-selection SHALL NOT select disabled or ineligible rows.
Shift-based multi-selection SHALL preserve previously selected rows unless explicitly deselected by a non-shift action.

#### Scenario: User adds multiple rows with shift-click
- **WHEN** a user has an existing selection anchor on an eligible row and performs `Shift+Click` on another eligible row
- **THEN** the grid selects the eligible rows in the interaction range
- **AND** any disabled rows in that range remain unselected

#### Scenario: User adds multiple rows with shift-space
- **WHEN** keyboard focus is on an eligible row and the user performs `Shift+Space`
- **THEN** the grid applies additive multi-selection using the current anchor and focused row
- **AND** disabled rows are skipped and remain unselected

#### Scenario: Shift multi-select does not alter disabled rows
- **WHEN** a shift-based selection interaction includes one or more disabled rows
- **THEN** disabled rows remain unselected
- **AND** selectable rows in scope are still processed for selection
