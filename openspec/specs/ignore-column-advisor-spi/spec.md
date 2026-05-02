# ignore-column-advisor-spi Specification

## Purpose
TBD - created by archiving change add-ignore-column-advisor-spi. Update Purpose after archive.
## Requirements
### Requirement: API exposes ignore-column advisor SPI
The `cfct-api` module SHALL expose an `IgnoreColumnAdvisor` SPI for deciding whether a column should be ignored during comparison.
The SPI SHALL operate on column metadata required for technical-column decisions.
Core comparison implementation SHALL support consulting multiple advisor implementations for one column decision.
A column SHALL be treated as ignored when at least one advisor returns an ignore decision.

#### Scenario: Multiple advisors contribute to one ignore decision
- **WHEN** core comparison evaluates a column and two or more `IgnoreColumnAdvisor` implementations are available
- **THEN** core comparison consults advisors and treats the column as ignored if any advisor indicates ignore

#### Scenario: No advisor ignores a column
- **WHEN** core comparison evaluates a column and all advisors indicate not ignored
- **THEN** the column remains eligible for compared-column partitioning

