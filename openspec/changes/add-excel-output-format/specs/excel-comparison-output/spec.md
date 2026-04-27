## ADDED Requirements

### Requirement: Comparison results can be rendered as an Excel workbook
The system SHALL render structured multi-table comparison results as an Excel workbook when Excel output is requested.
The workbook SHALL be a valid `.xlsx` document.
The workbook SHALL preserve the table order from the multi-table comparison result.

#### Scenario: Excel workbook is generated for multi-table result
- **WHEN** a configured comparison requests Excel output for multiple selected tables
- **THEN** the system produces a valid Excel workbook

#### Scenario: Excel workbook preserves requested table order
- **WHEN** a comparison result contains table results in a specific order
- **THEN** the workbook represents those table results in the same order after the Table of Contents sheet

### Requirement: Excel workbook starts with a Table of Contents sheet
The system SHALL include a first worksheet named `Table of Contents` in each Excel workbook.
The Table of Contents sheet SHALL summarize the workbook contents and comparison outcome.
The summary SHALL include one row per compared table.

#### Scenario: Table of Contents is the first sheet
- **WHEN** the system renders an Excel workbook
- **THEN** the first worksheet is named `Table of Contents`

#### Scenario: Table of Contents summarizes table results
- **WHEN** the system renders an Excel workbook for multiple compared tables
- **THEN** the Table of Contents contains one summary row for each compared table
- **AND** each summary row identifies the table and its difference counts

#### Scenario: Table of Contents identifies clean tables
- **WHEN** a compared table has no missing rows and no differing rows
- **THEN** the Table of Contents marks that table as having no differences

### Requirement: Excel workbook contains one detail sheet per compared table
The system SHALL include one detail worksheet for each table comparison result.
Each detail worksheet SHALL contain the table identity, business-key metadata, compared columns, ignored columns, rows only in left, rows only in right, and differing rows.
Worksheet names SHALL be deterministic and valid Excel sheet names.

#### Scenario: Detail sheet exists for each compared table
- **WHEN** the system renders an Excel workbook for two compared tables
- **THEN** the workbook contains two table detail worksheets after the Table of Contents sheet

#### Scenario: Detail sheet includes comparison metadata
- **WHEN** a table comparison result has business-key metadata, compared columns, and ignored columns
- **THEN** the table detail worksheet includes those values

#### Scenario: Detail sheet includes row differences
- **WHEN** a table comparison result has rows only in left, rows only in right, and differing matched rows
- **THEN** the table detail worksheet includes each group of row differences

#### Scenario: Detail sheet names are valid and deterministic
- **WHEN** compared table names contain characters or lengths that are invalid for Excel sheet names
- **THEN** the system uses safe deterministic worksheet names

### Requirement: Excel output is available through configured comparison execution
The configured comparison execution SHALL dispatch to the Excel renderer when the loaded comparison request specifies output type `excel`.
The configured execution SHALL keep JSON output behavior unchanged when the request specifies output type `json`.

#### Scenario: Excel request dispatches to Excel renderer
- **WHEN** a comparison request specifies output type `excel`
- **THEN** the configured comparison execution returns Excel workbook output for the comparison result

#### Scenario: JSON request remains unchanged
- **WHEN** a comparison request specifies output type `json`
- **THEN** the configured comparison execution returns deterministic JSON output using the existing JSON structure
