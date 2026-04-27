## MODIFIED Requirements

### Requirement: Detail sheets show actual differing rows in paired left/right columns
Each detail worksheet SHALL render one row per reportable difference item.
The first column SHALL summarize the result as `Only in left`, `Only in right`, or `Differ`.
Each business-key and compared column SHALL be represented by paired left and right Excel columns.
The detail table header SHALL use two rows for paired columns.
The top header row SHALL show each logical column name once across the paired left and right cells.
The second header row SHALL show `<<<` for the left cell and `>>>` for the right cell of each paired column.
The `Result` header cell SHALL span the two header rows.
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

#### Scenario: Detail header groups paired columns
- **WHEN** the system renders a detail worksheet
- **THEN** each business-key and compared column name appears once in the top header row spanning its left and right cells
- **AND** the row below contains `<<<` and `>>>` for the paired direction labels

#### Scenario: Detail sheet freezes key columns and headers
- **WHEN** the system renders a detail worksheet
- **THEN** the sheet freezes panes below the two-row difference-table header and after the result plus paired business-key columns
