## ADDED Requirements

### Requirement: Command grid supports member ID filter before interaction ID filter
The webapp SHALL provide a member ID filter control in the command selection section.
The webapp SHALL render the member ID filter control before the interaction ID filter control.
The command grid SHALL apply both member ID and interaction ID filters when provided.

#### Scenario: Member filter appears before interaction filter
- **WHEN** the drawer command section is rendered
- **THEN** the member ID filter control appears before the interaction ID filter control

#### Scenario: Member filter narrows command rows
- **WHEN** the user enters a member ID fragment in the member filter
- **THEN** only command rows whose member ID contains the fragment are shown

#### Scenario: Member and interaction filters combine
- **WHEN** the user enters both member ID and interaction ID filters
- **THEN** only rows matching both criteria are shown

#### Scenario: Clearing filters restores command rows
- **WHEN** the user clears member and interaction filters
- **THEN** all command rows are shown again
