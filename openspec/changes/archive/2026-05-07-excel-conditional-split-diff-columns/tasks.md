## 1. Layout Metadata and Header Planning

- [x] 1.1 Add a table-level layout analysis helper that marks each logical field as shared or split based on differing-row data.
- [x] 1.2 Ensure the layout analysis forces primary-key/business-key fields to shared mode.
- [x] 1.3 Refactor detail-sheet header generation to emit shared columns once and split columns as paired `<<<` and `>>>` headers.

## 2. Detail Row Rendering Updates

- [x] 2.1 Update detail-row cell writing to use precomputed layout metadata instead of inline `L:` / `R:` value formatting.
- [x] 2.2 Keep only-in-left and only-in-right behavior correct for split fields by populating only the existing side and leaving the opposite side blank.
- [x] 2.3 Preserve existing row-type labels and color-coding behavior while applying the new column layout.

## 3. Tests and Regression Coverage

- [x] 3.1 Update existing Excel renderer tests that assert inline prefixed values to assert header structure and raw cell payloads.
- [x] 3.2 Add or update tests proving that non-key fields split only when at least one differing row exists for that field.
- [x] 3.3 Add or update tests proving that key fields always remain shared and do not produce paired `<<<` and `>>>` columns.
