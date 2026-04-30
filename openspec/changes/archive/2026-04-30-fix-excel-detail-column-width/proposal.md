## Why

The Excel detail sheets currently autosize the second column, which can make layout inconsistent and harder to scan when the second column becomes wider or narrower than adjacent value columns.
This cosmetic fix keeps the detail sheet visual structure stable by ensuring the second column width matches the third column width.

## What Changes

- Stop autosizing column 2 on generated Excel detail sheets.
- Set column 2 width to exactly the same width used for column 3 on detail sheets.
- Keep existing autosizing and width behavior for other columns unchanged.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `excel-comparison-output`: Detail sheet column-width behavior is updated so column 2 is not autosized and matches column 3 width.

## Impact

- Affected code is in the Excel output generation logic that formats detail worksheets.
- No API, CLI, or dependency changes are expected.
- Existing Excel-related tests and snapshots may need updates if they assert exact column sizing behavior.
