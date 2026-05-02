# webapp-download-format-selector Specification

## Purpose
TBD - created by archiving change unify-webapp-download-actions-with-format-dropdown. Update Purpose after archive.
## Requirements
### Requirement: Webapp exposes a unified download action with selectable output format
The webapp SHALL provide one consolidated download control in the comparison results stage.
The consolidated control SHALL include a format selector with `json`, `yaml`, and `excel` choices.
The consolidated control SHALL trigger download for the currently selected format.
The default selected format SHALL be `json`.

#### Scenario: Default format is JSON
- **WHEN** comparison results are first rendered and download controls become visible
- **THEN** the format selector defaults to `json`

#### Scenario: Selected format determines downloaded artifact
- **WHEN** a user changes the format selector and activates the single download action
- **THEN** the downloaded file matches the selected format payload and extension

