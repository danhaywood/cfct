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
The command selection section SHALL provide a Refresh control on the same row as baseline controls.
The Refresh control SHALL be positioned to the left of the two baseline fields.
Activating Refresh SHALL reload command rows from the database-backed command source.
Activating Refresh SHALL clear selected command rows and selected business table rows.
Activating Refresh SHALL clear any previously rendered comparison progress status report.
Filtering SHALL narrow visible command rows without requiring a separate apply-filter action.
The command grid SHALL combine member, interactionId, replay-state, and baseline filters when a baseline is set or one or more replay-state checkboxes are selected.
When no replay-state checkboxes are selected, replay-state filtering SHALL be inactive.
When baseline is not set, baseline filtering SHALL be inactive.
When baseline is set, command rows with timestamp equal to or strictly after baseline SHALL remain visible.

#### Scenario: Baseline context-menu action sets baseline value
- **WHEN** the user opens the context menu for a command row and chooses set baseline from selected command
- **THEN** the baseline date/time picker is populated with the selected row timestamp

#### Scenario: Baseline filter keeps selected baseline command visible
- **WHEN** the user sets baseline from a selected command row
- **THEN** that selected command row remains visible in the command grid
- **AND** that selected command row appears as the first visible row in timestamp-ascending order

#### Scenario: Baseline filter limits rows to commands at or after baseline
- **WHEN** a baseline timestamp is set
- **THEN** only command rows with timestamp greater than or equal to baseline are visible

#### Scenario: Clearing baseline picker restores baseline-unfiltered rows
- **WHEN** the user clears the baseline date/time picker value
- **THEN** baseline filtering becomes inactive and command rows are filtered only by other active command filters
