## ADDED Requirements

### Requirement: Fixture creates logical-type mapping table in both logical databases
The harness SHALL create schema `_util` when it does not already exist in each logical database.
The harness SHALL create `_util.LogicalTypeTableMapping` in each logical database when it does not already exist.
The table SHALL include `logicalTypeName NVARCHAR(255) NULL` and `qualifiedName NVARCHAR(255) NOT NULL` columns.

#### Scenario: Mapping table exists after fixture schema initialization
- **WHEN** the harness initializes fixture schema for left and right logical databases
- **THEN** both logical databases contain `_util.LogicalTypeTableMapping` with `logicalTypeName` and `qualifiedName` columns

### Requirement: Fixture seeds logical-type mappings aligned to command and audit fixtures
The harness SHALL seed deterministic rows in `_util.LogicalTypeTableMapping` for logical types referenced by seeded command and audit fixture data.
The seeded rows SHALL include qualified table names that match the physical tables used by the fixture.

#### Scenario: Seeded mappings include command and audit logical types
- **WHEN** fixture data is initialized for a logical database
- **THEN** `_util.LogicalTypeTableMapping` contains rows for logical types referenced by seeded command and audit entries
- **AND** each row includes a non-null `qualifiedName` representing the mapped physical table

### Requirement: Fixture supports one logical type mapping to multiple physical tables
The harness SHALL allow multiple `_util.LogicalTypeTableMapping` rows with the same `logicalTypeName` and different `qualifiedName` values.
The seeded fixture data SHALL include at least one logical type with two or more qualified table mappings to represent inheritance with multiple `NEW_TABLE` mappings.

#### Scenario: Inheritance-style mapping is present
- **WHEN** fixture data is initialized for a logical database
- **THEN** at least one `logicalTypeName` appears in multiple mapping rows with distinct `qualifiedName` values
