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
The command grid SHALL provide a context menu action that copies current row details to clipboard.
The copied row details SHALL include command member and command interaction GUID.
The copied row-details payload SHALL use deterministic key/value text formatting suitable for ticket pasting.
The command selection section SHALL provide a Refresh control on the same row as baseline controls.
The Refresh control SHALL be positioned to the left of the two baseline fields.
Activating Refresh SHALL reload command rows from the database-backed command source.
Activating Refresh SHALL clear selected command rows and selected business table rows.
Activating Refresh SHALL clear any previously rendered comparison progress status report.
Filtering SHALL narrow visible command rows without requiring a separate apply-filter action.
The command grid SHALL combine member, interactionId, replay-state, and baseline filters when a baseline is set or one or more replay-state checkboxes are selected.
When no replay-state checkboxes are selected, replay-state filtering SHALL be inactive.
When baseline is not set, baseline filtering SHALL be inactive.
When baseline is set, command rows with timestamp greater than or equal to baseline SHALL remain visible.

#### Scenario: Context menu offers copy row details action
- **WHEN** the user opens the command-row context menu
- **THEN** a copy row details action is visible alongside existing row actions

#### Scenario: Copy row details writes member and interaction id
- **WHEN** the user activates copy row details on a command row
- **THEN** clipboard text contains that row's member value
- **AND** clipboard text contains that row's interactionId value

#### Scenario: Copied payload format is deterministic
- **WHEN** copy row details is activated
- **THEN** the clipboard payload uses deterministic key/value formatting for `member` and `interactionId`
