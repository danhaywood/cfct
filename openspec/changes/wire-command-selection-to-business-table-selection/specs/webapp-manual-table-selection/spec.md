## ADDED Requirements

### Requirement: Manual table grid accepts command-driven programmatic selections
The manual table grid SHALL accept programmatic selection updates from command footprint resolution.
Programmatic updates SHALL only affect rows that are present in the visible business table catalog.
Programmatic updates SHALL not fail when touched tables are unmapped or absent from the catalog.

#### Scenario: Command-driven selections apply only to visible business rows
- **WHEN** command footprint resolution returns a touched table set
- **THEN** only matching business table rows that exist in the manual table grid are selected
- **AND** unmatched touched tables are ignored

#### Scenario: Manual filtering remains stable with command-driven selections
- **WHEN** command-driven table selections are active
- **AND** the user applies or clears business table filters
- **THEN** selected-state consistency is preserved for the underlying selected table set

#### Scenario: Compare readiness reflects command-driven selected tables
- **WHEN** command-driven updates select one or more eligible business table rows
- **THEN** compare readiness is evaluated using the updated selected table set
