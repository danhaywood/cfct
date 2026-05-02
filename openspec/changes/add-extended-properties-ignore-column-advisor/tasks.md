## 1. Extended-Property Metadata Support

- [ ] 1.1 Extend SQL Server column metadata loading to include column-level extended property `cfct.ignored`.
- [ ] 1.2 Ensure missing extended-property values are handled without dropping columns from metadata results.
- [ ] 1.3 Add tests for metadata extraction of present and absent `cfct.ignored` values.

## 2. New Advisor Implementation

- [ ] 2.1 Add `IgnoreColumnAdvisorUsingExtendedProperties` as a Spring-managed advisor bean.
- [ ] 2.2 Implement truthy parsing for `cfct.ignored` values using case-insensitive accepted literals.
- [ ] 2.3 Ensure advisor participates in existing OR-composition with other advisors.

## 3. Typed Configuration

- [ ] 3.1 Add typed `@ConfigurationProperties` flag for enabling/disabling the extended-properties advisor.
- [ ] 3.2 Default this advisor flag to enabled.
- [ ] 3.3 Add or update configuration-binding tests for defaults and explicit disablement.

## 4. Comparison Behavior Coverage

- [ ] 4.1 Add or update comparison tests proving truthy `cfct.ignored` excludes a column from compared columns.
- [ ] 4.2 Add or update tests proving non-truthy/missing `cfct.ignored` leaves column behavior unchanged.
- [ ] 4.3 Add or update tests proving disabling the extended-properties advisor stops metadata-driven ignores.

## 5. Verification and Documentation

- [ ] 5.1 Run relevant module and integration tests covering advisor and metadata behavior.
- [ ] 5.2 Update README or configuration docs with the new advisor property and accepted truthy values.
- [ ] 5.3 Document SQL extended-property usage pattern for `cfct.ignored`.
