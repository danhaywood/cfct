# implementation-naming-conventions Specification

## Purpose
TBD - created by archiving change api-first-impl-decoupling-and-naming. Update Purpose after archive.
## Requirements
### Requirement: Interface implementations use interface-first naming
For classes that implement a named interface, implementation class names SHALL follow the pattern `<Interface><Qualifier>`.
The qualifier SHALL capture the implementation context, such as technology or mode, and SHALL be omitted only when there is a single canonical implementation.
New implementation classes SHALL NOT introduce the legacy `<Qualifier><Interface>` pattern.

#### Scenario: SQL Server executor follows interface-first naming
- **WHEN** a SQL Server-specific implementation of `CliComparisonExecutor` exists
- **THEN** the class name follows `CliComparisonExecutorSqlServer` and implements `CliComparisonExecutor`

#### Scenario: New implementation is rejected when using legacy prefix style
- **WHEN** a new class implementing an interface uses the `<Qualifier><Interface>` naming pattern
- **THEN** naming convention verification fails and requires renaming to interface-first style

### Requirement: Renames preserve wiring and behavior
Renaming implementation classes to satisfy interface-first naming SHALL preserve the same runtime wiring and behavior.
All Spring bean wiring, tests, and module references SHALL be updated consistently with renamed types.

#### Scenario: Renamed implementation remains injectable
- **WHEN** implementation classes are renamed to interface-first style
- **THEN** Spring wiring resolves the same interface contracts without runtime bean resolution failures

