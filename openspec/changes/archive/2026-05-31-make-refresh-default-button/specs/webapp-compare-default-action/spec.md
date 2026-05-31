## MODIFIED Requirements

### Requirement: Compare action supports Enter as default keyboard activation
The webapp SHALL treat Refresh as the default action for the selection workflow.
The webapp SHALL execute refresh when the user presses Enter while the selection workflow is active and refresh is enabled.
The webapp SHALL execute Enter-triggered refresh through the same orchestration path used by the Refresh button click.
The webapp SHALL ignore Enter-triggered activation when refresh is disabled.
The webapp SHALL continue to execute comparison only when the user explicitly activates the Compare button.

#### Scenario: Enter triggers refresh when refresh is enabled
- **WHEN** the user is in the selection workflow and refresh is enabled
- **THEN** pressing Enter starts refresh execution for the current command and table state

#### Scenario: Enter does nothing when refresh is disabled
- **WHEN** the selection workflow is active and refresh is disabled
- **THEN** pressing Enter does not start refresh or comparison execution

#### Scenario: Compare still runs on explicit compare activation
- **WHEN** the user explicitly activates the Compare button and compare is enabled
- **THEN** the webapp starts comparison execution through the compare orchestration path
