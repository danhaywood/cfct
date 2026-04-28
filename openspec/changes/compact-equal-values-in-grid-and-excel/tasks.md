## 1. Web grid compact-value rendering

- [ ] 1.1 Identify current column-construction logic for web comparison result tabs and add field-level compact-vs-paired classification.
- [ ] 1.2 Render one shared column for fields that are equal and paired `L:`/`R:` columns for fields that differ.
- [ ] 1.3 Keep row status badges and compare-stage interactions unchanged while applying compact rendering.

## 2. Excel detail-sheet compact-value rendering

- [ ] 2.1 Update Excel detail-sheet header generation to support shared columns for equal values and paired directional columns for differing values.
- [ ] 2.2 Update Excel detail-row writing to emit one cell for equal values and paired cells for differing values.
- [ ] 2.3 Preserve existing workbook structure, table ordering, and row classification semantics.

## 3. Test coverage updates

- [ ] 3.1 Update web result-grid tests to assert single-column rendering for equal values and paired columns for differing values.
- [ ] 3.2 Update Excel renderer tests to assert compact shared-cell output for equal values and directional paired-cell output for differences.
- [ ] 3.3 Run targeted module tests for web UI and Excel report rendering paths.

## 4. Documentation and screenshots

- [ ] 4.1 Capture updated screenshots that demonstrate compact equal-value rendering and paired differing-value rendering in the web UI.
- [ ] 4.2 Update README screenshots and surrounding text to describe the compact comparison presentation behavior.
