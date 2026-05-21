## MODIFIED Requirements

### Requirement: Compare run surfaces row-level completion cues and live counter
The webapp SHALL show per-table comparison completion cues in the manual business table grid while a compare run is active.
The webapp SHALL apply a completion background cue to a business table row as soon as that table comparison completes.
The webapp SHALL display a live completed counter adjacent to the Compare action using `completed of total` format.
The webapp SHALL update the completed counter each time a table completion or failure event is received.
The compare action row SHALL maintain explicit horizontal spacing between the completed counter label and the `Compare` button.
The compare action row SHALL prevent visual overlap between the completed counter label and the `Compare` button at supported viewport sizes.
The webapp SHALL clear row completion cues and hide the completed counter once the active run ends and a new selection workflow begins.

#### Scenario: Completed table row is highlighted during active run
- **WHEN** a compare run is active and a selected table finishes comparison
- **THEN** the corresponding business table row renders with the configured completion background cue

#### Scenario: Completed counter updates as tables finish
- **WHEN** a compare run starts with five selected tables
- **THEN** the compare-adjacent progress label starts at `0 of 5`
- **AND** advances to `1 of 5`, `2 of 5`, and so on as tables complete

#### Scenario: Completed counter is visually separated from Compare button
- **WHEN** the compare action row renders the completed counter and `Compare` button together
- **THEN** a visible horizontal gap exists between the counter label and button
- **AND** the label text does not overlap the button edge

#### Scenario: Counter and cues clear before next selection workflow
- **WHEN** a prior compare run has displayed row completion cues and a completed counter
- **AND** the user begins a new selection workflow
- **THEN** the webapp clears all prior row completion cues and hides the completed counter before the next run
