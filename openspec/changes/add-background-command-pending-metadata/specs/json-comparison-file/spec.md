## MODIFIED Requirements

### Requirement: Configured comparison produces deterministic detailed JSON output
The system SHALL render comparison results as deterministic JSON when the comparison file requests JSON output.
The JSON output SHALL preserve requested table order and stable difference ordering.
The JSON output SHALL include report-level difference status.
The JSON output SHALL include `comparedTables` with one table identity entry per compared table in request order.
The JSON output SHALL include `differingTables` with one detailed result entry per compared table that has missing rows or differing rows.
The JSON output SHALL omit clean compared tables from `differingTables`.
The JSON output SHALL support a top-level `command` metadata object when a caller supplies command context for the rendered comparison.
When command metadata is supplied, the JSON output SHALL include `command.interactionId` and `command.timestamp` fields.
The JSON output SHALL support a top-level `backgroundCommands` metadata object when a caller supplies background command status for the rendered comparison.
When background command status is supplied, the JSON output SHALL include a numeric `backgroundCommands.pending` field.
Each `differingTables` result SHALL include table identity, business-key metadata, compared columns, ignored columns, summary counts, rows only in left, rows only in right, and differing rows.
Rows only in left and rows only in right SHALL include the row key and available side values.
Differing rows SHALL include the row key, left values, right values, and changed column details.
The JSON output SHALL support empty comparison results with `hasDifferences` set to `false`, `comparedTables` set to an empty array, and `differingTables` set to an empty array.

#### Scenario: JSON output includes all compared table identities
- **WHEN** a configured comparison compares multiple selected tables
- **THEN** the JSON output contains `comparedTables` with one table identity entry per compared table in request order

#### Scenario: JSON output includes only differing table details
- **WHEN** a configured comparison compares multiple selected tables
- **AND** only some compared tables contain missing rows or differing rows
- **THEN** the JSON output contains `differingTables` entries only for tables with differences
- **AND** clean compared tables are omitted from `differingTables`

#### Scenario: JSON output includes table comparison metadata for differing tables
- **WHEN** a differing table result contains business-key metadata, compared columns, or ignored columns
- **THEN** the JSON output includes those values using stable field names in the corresponding `differingTables` entry

#### Scenario: JSON output includes table summary counts for differing tables
- **WHEN** a differing table result contains compared columns, ignored columns, missing rows, or differing rows
- **THEN** the JSON output includes summary counts for those items using stable field names in the corresponding `differingTables` entry

#### Scenario: JSON output includes rows only in left with values
- **WHEN** a differing table result contains a row only in the left database
- **THEN** the JSON output includes the row key and left-side values for that row in `differingTables`

#### Scenario: JSON output includes rows only in right with values
- **WHEN** a differing table result contains a row only in the right database
- **THEN** the JSON output includes the row key and right-side values for that row in `differingTables`

#### Scenario: JSON output includes differing row side-by-side values
- **WHEN** a matched row has differing compared values
- **THEN** the JSON output includes the row key, left-side values, right-side values, and changed column details for that row in `differingTables`

#### Scenario: JSON output includes supplied command metadata
- **WHEN** a caller renders JSON output with command metadata
- **THEN** the JSON output includes `command.interactionId` and `command.timestamp` fields matching the supplied command metadata
- **AND** the compared table and differing table structures remain present and deterministic

#### Scenario: JSON output includes supplied background command status
- **WHEN** a caller renders JSON output with background command status
- **THEN** the JSON output includes a numeric `backgroundCommands.pending` field matching the supplied pending count
- **AND** the compared table and differing table structures remain present and deterministic

#### Scenario: JSON output represents empty comparisons
- **WHEN** a configured comparison has no compared tables
- **THEN** the JSON output has `hasDifferences` set to `false`
- **AND** `comparedTables` is an empty array
- **AND** `differingTables` is an empty array

#### Scenario: JSON output is approval-tested
- **WHEN** the configured comparison integration test runs
- **THEN** the approved output is a deterministic detailed JSON document
