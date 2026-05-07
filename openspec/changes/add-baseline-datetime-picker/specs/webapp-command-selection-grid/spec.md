## MODIFIED Requirements

### Requirement: Command grid supports live filtering
The command grid SHALL provide filtering controls within a command-grid header row.
The command grid header row SHALL provide text filtering inputs for member and interactionId columns.
The command grid header row SHALL provide three replay-state filter checkboxes for `OK`, `PENDING`, and `FAILED`.
The replay-state checkboxes SHALL use compact labels `K`, `P`, and `F` mapped to `OK`, `PENDING`, and `FAILED` respectively.
The replay-state checkbox group SHALL be left-aligned within the replayState header filter cell.
The command selection section SHALL provide an optional baseline timestamp date/time picker above the command grid.
The baseline date/time picker SHALL support date and time selection through picker interaction.
The baseline date/time picker SHALL support direct user editing of the value.
The command grid SHALL provide a context menu action that sets baseline from the selected command row timestamp.
Filtering SHALL narrow visible command rows without requiring a separate apply-filter action.
The command grid SHALL combine member, interactionId, replay-state, and baseline filters when a baseline is set or one or more replay-state checkboxes are selected.
When no replay-state checkboxes are selected, replay-state filtering SHALL be inactive.
When baseline is not set, baseline filtering SHALL be inactive.
When baseline is set, only command rows with timestamp strictly after baseline SHALL remain visible.

#### Scenario: User filters command rows by visible identity values
- **WHEN** a user enters filter text for member or interactionId in the command-grid header row
- **THEN** only matching command rows remain visible in the grid without pressing an apply button

#### Scenario: Replay-state filter checkboxes are shown in header row
- **WHEN** the command-grid header filter row is rendered
- **THEN** replay-state filter checkboxes for `K`, `P`, and `F` are visible in the replayState filter cell

#### Scenario: Single replay-state checkbox narrows command rows
- **WHEN** the user selects one replay-state checkbox
- **THEN** only command rows matching that replay state remain visible

#### Scenario: Multiple replay-state checkboxes combine with OR semantics
- **WHEN** the user selects two or more replay-state checkboxes
- **THEN** command rows matching any selected replay state remain visible

#### Scenario: Replay-state filter combines with text filters
- **WHEN** one or more replay-state checkboxes are selected and member or interactionId filters are also provided
- **THEN** only rows matching all active text filters and any selected replay state remain visible

#### Scenario: Baseline date/time picker appears above command grid
- **WHEN** the command selection section is rendered
- **THEN** a baseline timestamp date/time picker is visible above the command grid

#### Scenario: Baseline context-menu action sets baseline value
- **WHEN** the user opens the context menu for a command row and chooses set baseline from selected command
- **THEN** the baseline date/time picker is populated with the selected row timestamp

#### Scenario: Baseline filter limits rows to commands after baseline
- **WHEN** a baseline timestamp is set
- **THEN** only command rows with timestamp strictly greater than baseline are visible

#### Scenario: Clearing baseline picker restores baseline-unfiltered rows
- **WHEN** the user clears the baseline date/time picker value
- **THEN** baseline filtering becomes inactive and command rows are filtered only by other active command filters
