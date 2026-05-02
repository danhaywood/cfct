## MODIFIED Requirements

### Requirement: Core library partitions columns for comparison
The system SHALL partition target-table columns into business-key columns, ignored columns, and compared columns.
Business-key columns SHALL be used for row matching.
Ignored columns SHALL be excluded from row matching and value comparison.
Compared columns SHALL include all remaining target-table columns.
The system SHALL determine ignored technical columns through composed `IgnoreColumnAdvisor` SPI implementations.
The core library SHALL consult an injected `List<IgnoreColumnAdvisor>` and treat a column as ignored when any advisor marks it ignored.
Built-in default advisors SHALL preserve current technical-column behavior when all default advisors are enabled.
Default technical behavior SHALL exclude identity-backed columns from compared columns.
Default technical behavior SHALL exclude columns named `uuid` or `guid` from compared columns using case-insensitive name matching.
Default technical behavior SHALL exclude columns with SQL Server datatype `UNIQUEIDENTIFIER` from compared columns.
Default technical behavior SHALL also support ignoring columns flagged by SQL Server extended property `cfct.ignored` with truthy values.
Built-in and custom advisor exclusions SHALL apply in addition to caller-provided ignored-column options.

#### Scenario: Default advisors exclude technical identifiers
- **WHEN** the target table contains columns named `id`, `version`, `guid`, and `uuid`
- **THEN** default comparison options exclude `version` and default advisors exclude identity-backed and guid/uuid technical columns from compared values

#### Scenario: Identity business-key column still matches rows
- **WHEN** a business-key index includes an identity-backed column
- **THEN** that column can still participate in row matching while remaining excluded from compared-value columns

#### Scenario: Uniqueidentifier datatype is excluded from compared values
- **WHEN** the target table contains a column with SQL Server datatype `UNIQUEIDENTIFIER`
- **THEN** that column is excluded from compared-value columns regardless of caller ignore options

#### Scenario: Non-key non-ignored business columns are compared
- **WHEN** the target table contains columns that are neither business-key columns nor ignored technical columns
- **THEN** those columns are compared for matched rows

#### Scenario: Any advisor can mark a column ignored
- **WHEN** one advisor in the injected advisor list marks a column ignored and others do not
- **THEN** the column is partitioned into ignored columns

#### Scenario: Extended-property advisor ignores marked column
- **WHEN** a column has SQL Server extended property `cfct.ignored` set to a truthy value
- **THEN** that column is partitioned into ignored columns
