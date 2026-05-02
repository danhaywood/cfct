## ADDED Requirements

### Requirement: Manual table selection state supports full reset from command section
The manual table selection state SHALL support clearing all selected business tables from a command-section clear action.
A command-section clear action SHALL remove both manual and command-driven programmatic table selections.

#### Scenario: Command clear removes programmatic and manual table selections
- **WHEN** the user triggers clear from the command section
- **THEN** all selected business table rows are deselected
- **AND** compare readiness is recalculated from an empty selected table set
