## Context

The project now supports a composed ignore-column advisor SPI with multiple default advisors.
Current advisors infer technical columns from identity flags, UUID conventions, and timestamp/version behavior.
The new request adds metadata-driven ignore decisions via SQL Server extended properties.
The target marker is `cfct.ignored` with truthy values.

## Goals / Non-Goals

**Goals:**
- Add a dedicated `IgnoreColumnAdvisorUsingExtendedProperties` implementation.
- Resolve `cfct.ignored` column metadata from SQL Server and ignore columns when the value is truthy.
- Keep advisor composition unchanged (any advisor can ignore).
- Provide a typed enable/disable property for this advisor, default enabled.

**Non-Goals:**
- Introduce non-SQL Server metadata providers in this change.
- Define a full custom expression language for metadata values.
- Remove existing technical-column advisors.

## Decisions

- Extend column metadata loading to include an `cfct.ignored` extended-property value per column.
This avoids per-column query overhead inside advisors and keeps decision data in existing metadata flow.
Alternative considered was querying extended properties lazily in the advisor for each column.
That was rejected due to repeated DB calls and complexity.

- Interpret truthy values case-insensitively using a bounded set: `true`, `1`, `yes`, `y`, `on`.
Any other non-null value is treated as false.
Alternative considered was any non-empty value means true.
That was rejected as too permissive and error-prone.

- Add `extendedPropertiesEnabled` (or equivalent kebab-case property mapping) to existing ignore-advisor properties, default true.
When disabled, advisor never ignores.

- Keep advisory OR semantics unchanged.
If extended property marks a column ignored, that column is ignored even if other advisors do not match.

## Risks / Trade-offs

- [Extended-property join could miss columns when metadata absent] → Use left join semantics so missing property does not hide column rows.
- [Truthy parsing ambiguity] → Document accepted truthy literals and test case-insensitive variants.
- [Behavior surprises when DB metadata changes] → Keep advisor independently toggleable via configuration.
