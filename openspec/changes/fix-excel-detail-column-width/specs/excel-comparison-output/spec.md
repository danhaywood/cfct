## MODIFIED Requirements

### Requirement: Excel workbook contains one detail sheet per compared table
The system SHALL include one detail worksheet for each table comparison result.
Each detail worksheet SHALL contain the table identity, business-key metadata, compared columns, ignored columns, rows only in left, rows only in right, and differing rows.
Worksheet names SHALL be deterministic and valid Excel sheet names.
Detail sheet column 2 SHALL NOT be autosized.
Detail sheet column 2 SHALL use the same width as detail sheet column 3.

#### Scenario: Detail sheet exists for each compared table
- **WHEN** the system renders an Excel workbook for two compared tables
- **THEN** the workbook contains two table detail worksheets after the Table of Contents sheet

#### Scenario: Detail sheet includes comparison metadata
- **WHEN** a table comparison result has business-key metadata, compared columns, and ignored columns
- **THEN** the table detail worksheet includes those values

#### Scenario: Detail sheet names are valid and deterministic
- **WHEN** compared table names contain characters or lengths that are invalid for Excel sheet names
- **THEN** the system uses safe deterministic worksheet names

#### Scenario: Detail sheet column 2 width matches column 3
- **WHEN** the system formats a detail worksheet
- **THEN** column 2 is not autosized
- **AND** column 2 width equals column 3 width
