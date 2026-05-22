## MODIFIED Requirements

### Requirement: Each result tab shows an Excel-like comparison grid
Each per-table tab SHALL render a Vaadin Grid that presents row-level comparison output in an Excel-like style.
The grid SHALL include row classification signals that distinguish matching rows, differing rows, and side-only rows.
For logical fields whose compared values are equal, the grid SHALL render one shared value column.
For logical fields whose compared values differ, the grid SHALL render paired `L:` and `R:` columns for that field.
The grid SHALL apply cell-level highlight styling for value differences and side-only missing values using deterministic semantic CSS classes.
The grid and its surrounding results content SHALL remain responsive within the comparison-stage container at varying viewport sizes.
The comparison-stage result content SHALL expand to consume the full available vertical extent in the main content region.
The result-grid viewport SHALL use the remaining vertical space below results controls and tabs.
The result-grid viewport SHALL reserve bottom clearance so fixed footer content does not overlap or obscure result rows.
The grid SHALL provide horizontal scrolling when rendered columns exceed the available results width.
The grid SHALL provide vertical scrolling when rendered rows exceed the available results height.
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

#### Scenario: Differing cells are highlighted
- **WHEN** a displayed row contains a logical field where left and right values differ
- **THEN** the corresponding value cells are rendered with the deterministic difference highlight class

#### Scenario: Missing-side cells are highlighted
- **WHEN** a displayed row exists only on one side
- **THEN** cells representing the missing side are rendered with deterministic missing-value highlight classes

#### Scenario: Responsive container bounds are preserved
- **WHEN** the page viewport is reduced and compared content is displayed
- **THEN** comparison widgets remain within the visible bounds of the comparison-stage container

#### Scenario: Result grid uses full available depth above footer
- **WHEN** comparison results are displayed in the right-side comparison stage
- **THEN** the active result-grid viewport expands to use the full available vertical space
- **AND** the viewport bottom remains above the fixed footer with no overlap

#### Scenario: Wide grids remain navigable
- **WHEN** compared output produces more columns than the visible results width
- **THEN** the user can horizontally scroll within the comparison results area to access off-screen columns

#### Scenario: Tall grids remain navigable
- **WHEN** compared output produces more rows than the visible results height
- **THEN** the user can vertically scroll within the comparison results area to access off-screen rows
