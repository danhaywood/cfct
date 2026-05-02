## ADDED Requirements

### Requirement: Compare action is below business table grid and visible on resize
The webapp SHALL render the compare action below the business table grid in the left navigation panel.
The webapp SHALL keep the compare action visible when the browser viewport is resized.
The webapp SHALL include visible spacer separation between command and business table sections.

#### Scenario: Drawer order places compare below business grid
- **WHEN** the home page drawer is rendered
- **THEN** the command section appears first
- **AND** the business table grid appears before the compare action

#### Scenario: Compare action remains visible during resized viewport usage
- **WHEN** the browser viewport is resized to a smaller height
- **THEN** the compare action remains visible and reachable without losing context of selected tables

#### Scenario: Spacer separates command and business table sections
- **WHEN** the drawer is rendered
- **THEN** a spacer element is present between the command selection area and the business table selection area
