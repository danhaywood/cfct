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

### Requirement: Detail sheets show actual differing rows in paired left/right columns
Each detail worksheet SHALL render one row per reportable difference item.
The first column SHALL summarize the result as `Only in left`, `Only in right`, or `Differ`.
The worksheet SHALL determine column layout per logical field across the table result before writing detail headers and rows.
For primary-key or business-key logical fields, the worksheet SHALL always render one shared column.
For non-key logical fields whose values differ in at least one reported differing row, the worksheet SHALL render paired directional columns labeled `<<<` and `>>>`.
For non-key logical fields whose values do not differ in any reported differing row, the worksheet SHALL render one shared column.
Missing-row entries SHALL populate only the side where the row exists for split fields and SHALL keep the opposite side blank.
Differing-row entries SHALL populate both directional cells for split fields and a shared value cell for non-split fields.
Excel cell values SHALL NOT embed `L:` or `R:` prefixes because direction is represented by column position and header labels.

#### Scenario: Key field is rendered as one shared column
- **WHEN** a table detail worksheet includes primary-key or business-key fields
- **THEN** each key field appears once as a shared column
- **AND** the worksheet does not emit `<<<` and `>>>` paired columns for key fields

#### Scenario: Field with at least one differing row is rendered as paired columns
- **WHEN** any reported differing row contains unequal left and right values for a non-key logical field
- **THEN** the worksheet header includes paired `<<<` and `>>>` columns for that field
- **AND** each differing row populates both directional cells for that field

#### Scenario: Field with no differing rows is rendered once
- **WHEN** all reported differing rows contain equal left and right values for a non-key logical field
- **THEN** the worksheet header includes one shared column for that field
- **AND** the worksheet does not emit directional paired columns for that field

#### Scenario: Only-in-left row populates left split cells only
- **WHEN** a table comparison result contains a row only in left and the field is split
- **THEN** the detail worksheet includes one row marked `Only in left`
- **AND** the row populates only the `<<<` cell for that field and leaves the `>>>` cell blank

#### Scenario: Excel values do not include inline side prefixes
- **WHEN** the system renders split fields in detail worksheets
- **THEN** cell payloads contain raw values without `L:` or `R:` prefixes
- **AND** side information is conveyed only by `<<<` and `>>>` header columns

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

