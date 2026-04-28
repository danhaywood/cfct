## 1. Locate and update detail-sheet column sizing logic

- [ ] 1.1 Identify the Excel detail-sheet writer code path that currently autosizes column 2 and column 3.
- [ ] 1.2 Update detail-sheet formatting so column 2 is excluded from autosize operations.
- [ ] 1.3 Set column 2 width to the same width value applied to column 3.

## 2. Verify behavior with tests and output checks

- [ ] 2.1 Add or update automated tests to assert detail-sheet column 2 is not autosized and matches column 3 width.
- [ ] 2.2 Regenerate or inspect representative Excel output to confirm no unintended layout changes in other columns.
- [ ] 2.3 Run the relevant module test suite and ensure all Excel output tests pass.
