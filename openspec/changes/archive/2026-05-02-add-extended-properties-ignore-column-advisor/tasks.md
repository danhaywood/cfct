## 1. Extended-Property Metadata Support

- [x] 1.1 Extend SQL Server column metadata loading to include column-level extended property `cfct.ignored`.
- [x] 1.2 Ensure missing extended-property values are handled without dropping columns from metadata results.
- [x] 1.3 Add tests for metadata extraction of present and absent `cfct.ignored` values.

## 2. New Advisor Implementation

- [x] 2.1 Add `IgnoreColumnAdvisorUsingExtendedProperties` as a Spring-managed advisor bean.
- [x] 2.2 Implement truthy parsing for `cfct.ignored` values using case-insensitive accepted literals.
- [x] 2.3 Ensure advisor participates in existing OR-composition with other advisors.

## 3. Typed Configuration

- [x] 3.1 Add typed `@ConfigurationProperties` flag for enabling/disabling the extended-properties advisor.
- [x] 3.2 Default this advisor flag to enabled.
- [x] 3.3 Add or update configuration-binding tests for defaults and explicit disablement.

## 4. Comparison Behavior Coverage

- [x] 4.1 Add or update comparison tests proving truthy `cfct.ignored` excludes a column from compared columns.
- [x] 4.2 Add or update tests proving non-truthy/missing `cfct.ignored` leaves column behavior unchanged.
- [x] 4.3 Add or update tests proving disabling the extended-properties advisor stops metadata-driven ignores.

## 5. Verification and Documentation

- [x] 5.1 Run relevant module and integration tests covering advisor and metadata behavior.
- [x] 5.2 Update README or configuration docs with the new advisor property and accepted truthy values.
- [x] 5.3 Document SQL extended-property usage pattern for `cfct.ignored`.
