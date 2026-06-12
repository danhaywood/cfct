## ADDED Requirements

### Requirement: Manual table eligibility uses distinct PK-suffixed key candidates
The webapp SHALL evaluate manual business-table eligibility using distinct SQL Server unique index or unique constraint candidates whose names end with the literal configured business-key suffix.
The default suffix SHALL be `_PK`.
The suffix match SHALL be case-insensitive.
The suffix match SHALL treat `_` as a literal underscore rather than a SQL wildcard.
A table SHALL be eligible when exactly one unique index or unique constraint matches the configured suffix.
A table SHALL be eligible when more than one unique index or unique constraint matches the configured suffix and exactly one matching object is the table's primary key.
A table SHALL remain ineligible when no unique index or unique constraint matches the configured suffix.
A table SHALL remain ineligible when more than one matching object exists and no single matching primary-key object disambiguates the candidates.
Table-level extended properties SHALL NOT multiply a single matching key object into an apparent multiple-candidate eligibility failure.
Unique indexes or unique constraints that do not match the configured suffix SHALL NOT make an otherwise eligible table ineligible.

#### Scenario: Primary key plus unrelated unique constraint is eligible
- **WHEN** a business table has a primary-key constraint named `ApplicationUser_PK`
- **AND** the same table has a unique constraint named `ApplicationUser__username__UNQ`
- **THEN** the manual table grid treats the table as eligible

#### Scenario: Table-level extended properties do not multiply key candidates
- **WHEN** a business table has exactly one unique index or unique constraint whose name ends with `_PK`
- **AND** the same table has one or more table-level extended properties that do not disable the table
- **THEN** the manual table grid treats the table as eligible

#### Scenario: Literal suffix matching avoids SQL wildcard false positives
- **WHEN** a business table has a unique index or unique constraint whose name does not literally end with `_PK`
- **THEN** that object does not count as a matching manual-selection eligibility candidate

#### Scenario: Primary key disambiguates multiple matching candidates
- **WHEN** a business table has multiple unique indexes or unique constraints whose names end with `_PK`
- **AND** exactly one matching object is the table's primary key
- **THEN** the manual table grid treats the table as eligible

#### Scenario: Multiple non-primary matching candidates remain ineligible
- **WHEN** a business table has multiple unique indexes or unique constraints whose names end with `_PK`
- **AND** no single matching object is the table's primary key
- **THEN** the manual table grid treats the table as ineligible with an ambiguity reason
