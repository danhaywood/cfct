## ADDED Requirements

### Requirement: Compare action supports Enter as default keyboard activation
The webapp SHALL treat Compare as the default action for the selection workflow when at least one eligible table is selected.
The webapp SHALL execute comparison when the user presses Enter while the selection workflow is active and compare is enabled.
The webapp SHALL execute Enter-triggered comparison through the same orchestration path used by the Compare button click.
The webapp SHALL ignore Enter-triggered compare activation when compare is disabled.

#### Scenario: Enter triggers compare when eligible tables are selected
- **WHEN** the user has one or more eligible tables selected and compare is enabled
- **THEN** pressing Enter starts comparison execution for the current selected eligible tables

#### Scenario: Enter does nothing when compare is disabled
- **WHEN** no eligible table is selected and compare is disabled
- **THEN** pressing Enter does not start comparison execution
