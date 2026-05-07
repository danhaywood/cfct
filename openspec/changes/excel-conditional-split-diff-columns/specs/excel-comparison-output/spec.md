## MODIFIED Requirements

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
