## MODIFIED Requirements

### Requirement: Detail sheets show actual differing rows in paired left/right columns
Each detail worksheet SHALL render one row per reportable difference item.
The first column SHALL summarize the result as `Only in left`, `Only in right`, or `Differ`.
For logical fields whose values are equal in a reported row, the worksheet SHALL render one shared value cell for that field.
For logical fields whose values differ in a reported row, the worksheet SHALL render paired left and right cells for that field.
The detail table header SHALL represent shared columns once and directional columns as paired `L:` and `R:` labels.
Missing-row entries SHALL populate only the side where the row exists.
Differing-row entries SHALL populate both sides only for fields that differ.

#### Scenario: Equal field value is rendered once in detail row
- **WHEN** a reported row includes a logical field with equal left and right values
- **THEN** the detail worksheet shows one shared value cell for that field

#### Scenario: Differing field value is rendered in paired cells
- **WHEN** a reported row includes a logical field with differing left and right values
- **THEN** the detail worksheet shows paired directional cells for that field

#### Scenario: Only-in-left row shows left values only
- **WHEN** a table comparison result contains a row only in left
- **THEN** the detail worksheet includes one row marked `Only in left`
- **AND** the row populates the left-side values for available fields and leaves right-side directional values blank

#### Scenario: Only-in-right row shows right values only
- **WHEN** a table comparison result contains a row only in right
- **THEN** the detail worksheet includes one row marked `Only in right`
- **AND** the row populates the right-side values for available fields and leaves left-side directional values blank
