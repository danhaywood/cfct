## Why

The project now has a SQL Server two-database harness and a realistic `PurchaseOrder` fixture, but it does not yet have a reusable comparison library.
The next step is to introduce the first core library capability: comparing one named table between two SQL Server databases by business key.

## What Changes

- Add a Spring-friendly core library API for comparing a single named table between left and right JDBC connections.
- Allow the core library to use Spring Boot wiring patterns such as `@Component`, `@Service`, and `@Configuration` where they make consumption easier.
- Keep the core library independent of CLI and web-specific APIs.
- Add SQL Server metadata discovery for the target table, including columns, identity columns, and a unique business-key index whose name ends with `_BK`.
- Match rows using the business-key index columns rather than identity primary key values.
- Compare non-key, non-ignored columns and return a structured comparison result.
- Provide default comparison options for the first slice, including `_BK` as the business-key index suffix and `id` / `version` as ignored column names.
- Add a deterministic text report renderer so the first comparison behavior can be characterized with Approval tests.
- Use the existing realistic `PurchaseOrder` fixture to verify the library behavior.
- Do not add a CLI, webapp, multi-table orchestration, schema comparison, or cross-database support in this change.

## Capabilities

### New Capabilities

- `core-single-table-comparison`: Provides the core library capability for comparing one SQL Server table between two JDBC connections by business key.

### Modified Capabilities

- None.

## Impact

- Adds production Java types for the comparison core, SQL Server metadata and row-reading support, comparison results, and report rendering.
- Adds integration tests that run against the SQL Server harness and `PurchaseOrder` fixture.
- Uses existing dependencies where possible and avoids introducing CLI or web-specific dependencies into the core library.
- Establishes the library-first boundary that future CLI and webapp layers will call into, potentially through Spring-managed services.
