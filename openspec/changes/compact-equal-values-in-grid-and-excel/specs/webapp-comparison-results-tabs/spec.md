## MODIFIED Requirements

### Requirement: Each result tab shows an Excel-like comparison grid
Each per-table tab SHALL render a Vaadin Grid that presents row-level comparison output in an Excel-like style.
The grid SHALL include row classification signals that distinguish matching rows, differing rows, and side-only rows.
For logical fields whose compared values are equal, the grid SHALL render one shared value column.
For logical fields whose compared values differ, the grid SHALL render paired `L:` and `R:` columns for that field.
The grid SHALL keep table identity and row-order presentation deterministic for test assertions.

#### Scenario: Equal values are rendered once
- **WHEN** a result tab is opened for rows where a logical field has equal left and right values
- **THEN** that field is rendered as a single shared value column without paired `L:` and `R:` columns

#### Scenario: Differing values keep paired columns
- **WHEN** a result tab is opened for rows where a logical field has differing left and right values
- **THEN** that field is rendered with paired `L:` and `R:` columns

#### Scenario: Grid highlights row difference classification
- **WHEN** compared rows include differences or side-only rows
- **THEN** the grid visually indicates row classification for those rows using Excel-like status color coding
