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
The command selection section SHALL support accelerator keys `F5` and `Alt+R` to activate Refresh.
When accelerator-triggered Refresh is consumed by the app, browser-level page reload SHALL NOT occur.
Activating Refresh SHALL clear selected command rows and selected business table rows before applying any auto-selection.
Activating Refresh SHALL auto-select exactly one command row when one or more refreshed rows have replay state `OK`.
Activating Refresh SHALL auto-select the `OK` command row with the most recent timestamp.
Activating Refresh SHALL keep keyboard focus on the auto-selected command row.
Activating Refresh SHALL clear any previously rendered comparison progress status report.
Filtering SHALL narrow visible command rows without requiring a separate apply-filter action.
The command grid SHALL combine member, interactionId, replay-state, and baseline filters when a baseline is set or one or more replay-state checkboxes are selected.
When no replay-state checkboxes are selected, replay-state filtering SHALL be inactive.
When baseline is not set, baseline filtering SHALL be inactive.
When baseline is set, command rows with timestamp equal to or strictly after baseline SHALL remain visible.

#### Scenario: F5 triggers command refresh
- **WHEN** keyboard focus is within the command selection area
- **AND** the user presses `F5`
- **THEN** the command refresh action runs through the same logic path as clicking Refresh

#### Scenario: Alt+R triggers command refresh
- **WHEN** keyboard focus is within the command selection area
- **AND** the user presses `Alt+R`
- **THEN** the command refresh action runs through the same logic path as clicking Refresh

#### Scenario: Refresh accelerators do not trigger while typing
- **WHEN** focus is in a text/date/time input field
- **AND** the user presses `F5` or `Alt+R`
- **THEN** command refresh accelerator handling is ignored
