## ADDED Requirements

### Requirement: Command reselection cancels active comparison execution
The webapp SHALL cancel an in-progress comparison run when a user selects an individual command row.
The cancellation SHALL complete before the newly selected command is applied to comparison eligibility state.
The cancellation flow SHALL use the same user-visible cancellation semantics as explicit comparison cancel actions.
After cancellation from command reselection, transient comparison progress indicators and stale status report content SHALL be cleared.

#### Scenario: Selecting a command during compare cancels active run
- **WHEN** a comparison run is in progress
- **AND** the user selects an individual command row
- **THEN** the in-progress comparison run is cancelled
- **AND** the new command selection is applied after cancellation

#### Scenario: Reselection cancellation clears transient comparison state
- **WHEN** a comparison run is cancelled due to command row selection
- **THEN** progress indicators for the cancelled run are cleared
- **AND** stale comparison status report content is removed before the next compare action
