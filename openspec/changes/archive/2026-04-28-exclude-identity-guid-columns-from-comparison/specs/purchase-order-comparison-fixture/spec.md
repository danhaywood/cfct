## MODIFIED Requirements

### Requirement: Fixture data anticipates future row comparison scenarios
The fixture SHALL seed left and right purchase order data that can support future row comparison tests without implementing comparison behavior in this change.
The data SHALL include cases for equal rows, differing domain values, rows present only on one side, rows where only version values differ, and rows where identity values differ.
The fixture SHALL include at least one technical-identifier difference case that demonstrates ignored comparison behavior for identity and GUID-style columns.

#### Scenario: Fixture includes matching business references
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases with the same domain values

#### Scenario: Fixture includes differing domain values
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases with different comparable domain values

#### Scenario: Fixture includes side-specific rows
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference exists only in the left database and at least one purchase order reference exists only in the right database

#### Scenario: Fixture includes version-only difference example
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases where domain values and identity values match but `version` values differ

#### Scenario: Fixture includes identity-difference example
- **WHEN** the purchase order fixture data is loaded into both logical databases
- **THEN** at least one purchase order reference appears in both databases where domain values match but identity values differ

#### Scenario: Identity-only and guid-only differences are available for ignore characterization
- **WHEN** fixture consumers compare rows that differ only in identity or GUID-style technical columns
- **THEN** those rows can be used to verify that technical-identifier-only differences are ignored by default comparison behavior
