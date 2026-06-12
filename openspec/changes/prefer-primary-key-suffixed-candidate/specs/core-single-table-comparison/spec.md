## MODIFIED Requirements

### Requirement: Core library discovers business-key metadata
The system SHALL discover the target table's business key from SQL Server metadata using a unique index or unique constraint whose name ends with the configured business-key suffix.
The default suffix SHALL be `_PK`.
The suffix match SHALL be case-insensitive.
The suffix match SHALL treat the suffix as literal text.
The discovery logic SHALL accept prefixed or compound object names as long as the full identifier ends with the configured suffix.
When exactly one unique index or unique constraint matches the configured suffix, the system SHALL use that object's key columns as the row matching key.
When more than one unique index or unique constraint matches the configured suffix and exactly one matching object is the table's primary key, the system SHALL use the primary-key object's key columns as the row matching key.
When more than one matching object remains and no single matching primary-key object disambiguates the candidates, the system SHALL fail clearly as ambiguous.

#### Scenario: PK-suffixed unique index identifies row key
- **WHEN** the target table has exactly one unique index whose name ends with `_PK`
- **THEN** the core library uses that index's key columns as the row matching key

#### Scenario: PK-suffixed unique constraint identifies row key
- **WHEN** the target table has exactly one unique constraint whose name ends with `_PK`
- **THEN** the core library uses that constraint's key columns as the row matching key

#### Scenario: Compound PK-suffixed object name identifies row key
- **WHEN** the target table has a unique index or unique constraint named `PurchaseOrder__reference__PK`
- **THEN** the core library accepts it as matching the configured business-key suffix and uses its key columns for row matching

#### Scenario: Composite business key metadata is represented
- **WHEN** the PK-suffixed unique index or unique constraint contains more than one key column
- **THEN** the core library represents all key columns in ordinal order as the row matching key

#### Scenario: PK-suffixed primary key disambiguates other matching unique objects
- **WHEN** the target table has one primary-key constraint whose name ends with `_PK`
- **AND** the target table has one or more additional unique indexes or unique constraints whose names also end with `_PK`
- **THEN** the core library uses the primary-key constraint's key columns as the row matching key

#### Scenario: Unrelated unique constraint does not create ambiguity
- **WHEN** the target table has a primary-key constraint named `ApplicationUser_PK`
- **AND** the target table has a unique constraint named `ApplicationUser__username__UNQ`
- **THEN** the core library uses `ApplicationUser_PK` as the row matching key

#### Scenario: Missing business-key object fails clearly
- **WHEN** the target table has no unique index or unique constraint whose name ends with the configured business-key suffix
- **THEN** the comparison fails with an error that identifies the table and the missing business-key convention

#### Scenario: Ambiguous business-key objects fail clearly
- **WHEN** the target table has more than one unique index or unique constraint whose name ends with the configured business-key suffix
- **AND** no single matching object is the table's primary key
- **THEN** the comparison fails with an error that identifies the table and the ambiguous business-key objects
