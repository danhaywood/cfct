## ADDED Requirements

### Requirement: Baseline timestamp field is available above the command selection grid
The webapp SHALL render an optional baseline timestamp field immediately above the command selection grid.
The baseline field SHALL default to empty.
The baseline field SHALL allow users to clear the value to disable baseline filtering.

#### Scenario: Baseline field is shown with empty default
- **WHEN** the command selection area is rendered for a new page state
- **THEN** the baseline timestamp field is visible above the command selection grid
- **AND** the baseline timestamp value is empty

#### Scenario: Clearing baseline disables baseline filtering
- **WHEN** a baseline timestamp is currently set and the user clears the baseline field
- **THEN** baseline filtering is disabled
- **AND** command rows are no longer restricted by the baseline cutoff

### Requirement: Baseline timestamp can be edited directly
The webapp SHALL allow direct manual editing of the baseline timestamp value.
The webapp SHALL validate edited baseline input against the accepted command timestamp format.
Invalid baseline values SHALL NOT be applied to command filtering.

#### Scenario: Valid baseline edit applies filtering state
- **WHEN** the user enters a valid baseline timestamp in the baseline field
- **THEN** the baseline value is stored as active filter state
- **AND** command rows are filtered using the active baseline cutoff

#### Scenario: Invalid baseline edit is rejected
- **WHEN** the user enters a baseline timestamp that does not match the accepted format
- **THEN** the webapp keeps the previous valid baseline value or empty state
- **AND** the invalid value is not used to filter command rows

### Requirement: Baseline can be set from the selected command via context menu
The command selection grid SHALL provide a context menu action to set baseline from the selected command timestamp.
The context menu action SHALL use the timestamp from the row on which the menu action is invoked.
Applying the action SHALL update baseline state and refresh visible command rows.

#### Scenario: User sets baseline from a selected command row
- **WHEN** the user invokes the row context menu and chooses set baseline from selected command
- **THEN** the baseline timestamp field is populated with that row timestamp
- **AND** the command grid refreshes using the new baseline cutoff
