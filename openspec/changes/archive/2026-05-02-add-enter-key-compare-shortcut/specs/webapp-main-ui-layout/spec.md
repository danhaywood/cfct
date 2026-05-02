## MODIFIED Requirements

### Requirement: Comparison area exposes a placeholder compare action
The webapp SHALL provide a `Compare` button in the navigation-area selection stage.
The `Compare` button SHALL be rendered above the table-selection grid.
The `Compare` button SHALL be aligned to the right within its action row.
The `Compare` action row SHALL include top margin that visually aligns the compare control block with the height rhythm of the navbar hamburger/menu row.
The `Compare` button SHALL be disabled when no eligible table is selected.
The `Compare` button SHALL be enabled when one or more eligible tables are selected.
Activating the `Compare` button SHALL execute comparison orchestration for all currently selected eligible tables.
Pressing Enter in the active selection workflow SHALL activate compare when the `Compare` action is enabled.
The comparison stage SHALL render the execution outcome in the right-side content area.

#### Scenario: Compare button is disabled before selection
- **WHEN** the home page is rendered and no eligible tables are selected
- **THEN** the `Compare` button is disabled

#### Scenario: Compare button is enabled after selection
- **WHEN** a user selects at least one eligible table
- **THEN** the `Compare` button becomes enabled

#### Scenario: Compare button is positioned above table catalog
- **WHEN** the selection stage is visible in the navigation panel
- **THEN** the `Compare` button appears above the table grid and aligned to the right

#### Scenario: Compare action row aligns with navbar rhythm
- **WHEN** the selection stage is visible next to the main content area
- **THEN** the compare action row includes top spacing that visually aligns with the navbar hamburger/menu row height

#### Scenario: Compare button executes selected-table comparison
- **WHEN** a user activates the enabled `Compare` button
- **THEN** comparison is executed for the current selected eligible tables and the right-side comparison stage updates with resulting content

#### Scenario: Enter activates compare when enabled
- **WHEN** compare is enabled in the selection workflow and the user presses Enter
- **THEN** comparison is executed for the current selected eligible tables and the comparison stage updates with resulting content
