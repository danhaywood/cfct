## 1. Core metadata and option defaults

- [x] 1.1 Update default comparison options to use `_PK` suffix for business-key index discovery.
- [x] 1.2 Remove implicit `id` from default ignored columns so identity values are compared by default.
- [x] 1.3 Update SQL Server metadata-reader logic and error messages to reflect `_PK`-suffix conventions.
- [x] 1.4 Add or update single-table unit tests for `_PK` discovery, missing `_PK`, and ambiguous `_PK` behaviors.

## 2. Comparison behavior and output expectations

- [x] 2.1 Update single-table comparison tests to assert identity differences are reported unless `id` is explicitly ignored.
- [x] 2.2 Update multi-table comparison tests to assert `_PK` index usage per table and updated ignored-column defaults.
- [x] 2.3 Refresh deterministic text, JSON, and Excel expectation artifacts affected by key-name and ignored-column changes.

## 3. Fixture migration

- [x] 3.1 Rename fixture business-key indexes from `_BK` to `_PK` for good comparable tables.
- [x] 3.2 Keep negative fixtures without `_PK` indexes and update fixture assertions accordingly.
- [x] 3.3 Update fixture data cases so version-only differences and identity-difference expectations match new defaults.

## 4. Webapp manual-selection eligibility migration

- [x] 4.1 Update webapp table-catalog eligibility logic from `_BK` rule to `_PK` rule.
- [x] 4.2 Update webapp unit tests for eligible and ineligible table rows under `_PK` convention.
- [x] 4.3 Update Playwright fixture preparation and assertions to validate `_PK`-based selection eligibility.

## 5. Documentation and verification

- [x] 5.1 Update README and relevant docs to describe `_PK` default behavior and identity-column comparison default.
- [x] 5.2 Run webapp unit tests and headless Playwright tests with updated eligibility and fixture conventions.
- [x] 5.3 Run affected module or reactor tests to verify no regressions under the `_PK` and identity-aware defaults.
