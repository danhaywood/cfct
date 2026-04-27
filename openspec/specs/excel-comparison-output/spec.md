# excel-comparison-output Specification

## Purpose
TBD - created by archiving change add-excel-output-format. Update Purpose after archive.
## Requirements
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

### Requirement: Excel workbook starts with a navigable Table of Contents sheet
The system SHALL include a first worksheet named `Table of Contents` in each Excel workbook.
The Table of Contents sheet SHALL summarize the workbook contents and comparison outcome.
The summary SHALL include one row per compared table.
The table-name cell in each summary row SHALL be a hyperlink to the corresponding detail worksheet.
The Table of Contents sheet SHALL freeze panes at `B2`.

#### Scenario: Table of Contents is the first sheet
- **WHEN** the system renders an Excel workbook
- **THEN** the first worksheet is named `Table of Contents`

#### Scenario: Table of Contents summarizes table results
- **WHEN** the system renders an Excel workbook for multiple compared tables
- **THEN** the Table of Contents contains one summary row for each compared table
- **AND** each summary row identifies the table and its difference counts

#### Scenario: Table of Contents links to detail sheets
- **WHEN** the system renders an Excel workbook for compared tables
- **THEN** each Table of Contents table-name cell links to that table's detail worksheet

#### Scenario: Table of Contents freezes table names and headers
- **WHEN** the system renders an Excel workbook
- **THEN** the Table of Contents sheet freezes panes at `B2`

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

#### Scenario: Detail sheet names are valid and deterministic
- **WHEN** compared table names contain characters or lengths that are invalid for Excel sheet names
- **THEN** the system uses safe deterministic worksheet names

### Requirement: Detail sheets show actual differing rows in paired left/right columns
Each detail worksheet SHALL render one row per reportable difference item.
The first column SHALL summarize the result as `Only in left`, `Only in right`, or `Differ`.
Each business-key and compared column SHALL be represented by paired left and right Excel columns.
Missing-row entries SHALL populate only the side where the row exists.
Differing-row entries SHALL populate both sides on the same Excel row.

#### Scenario: Only-in-left row shows left values only
- **WHEN** a table comparison result contains a row only in left
- **THEN** the detail worksheet includes one row marked `Only in left`
- **AND** the row populates the left columns and leaves the corresponding right columns blank

#### Scenario: Only-in-right row shows right values only
- **WHEN** a table comparison result contains a row only in right
- **THEN** the detail worksheet includes one row marked `Only in right`
- **AND** the row populates the right columns and leaves the corresponding left columns blank

#### Scenario: Differing row shows both sides on one row
- **WHEN** a matched row has differing values
- **THEN** the detail worksheet includes one row marked `Differ`
- **AND** the row contains both left and right values for the business key and compared columns

#### Scenario: Detail sheet freezes key columns and headers
- **WHEN** the system renders a detail worksheet
- **THEN** the sheet freezes panes below the difference-table header and after the result plus paired business-key columns

### Requirement: Detail sheets colour-code comparison outcomes
The system SHALL colour-code detail worksheet cells to support manual inspection.
Only-in-left rows SHALL use light yellow.
Only-in-right rows SHALL use a darker yellow.
Matching cells in differing rows SHALL use green.
Differing value cells SHALL use pink or red.

#### Scenario: Missing rows are colour-coded by side
- **WHEN** a detail worksheet contains rows only in left and rows only in right
- **THEN** the only-in-left row uses light yellow
- **AND** the only-in-right row uses a darker yellow

#### Scenario: Matched differing rows colour-code cells
- **WHEN** a matched row has both matching and differing compared values
- **THEN** matching value cells use green
- **AND** differing value cells use pink or red

### Requirement: Excel output is available through configured comparison execution
The configured comparison execution SHALL dispatch to the Excel renderer when the loaded comparison request specifies output type `excel`.
The configured execution SHALL keep JSON output behavior unchanged when the request specifies output type `json`.

#### Scenario: Excel request dispatches to Excel renderer
- **WHEN** a comparison request specifies output type `excel`
- **THEN** the configured comparison execution returns Excel workbook output for the comparison result

#### Scenario: JSON request remains unchanged
- **WHEN** a comparison request specifies output type `json`
- **THEN** the configured comparison execution returns deterministic JSON output using the existing JSON structure

