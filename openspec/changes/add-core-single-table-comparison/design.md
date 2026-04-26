## Context

The project direction is library first, then CLI, and potentially webapp later.
The SQL Server harness and `PurchaseOrder` fixture now provide a realistic left/right database scenario for the first library behavior.
The first comparison slice should compare one caller-specified table, while keeping the design open for later multi-table orchestration.

The core comparison code should be plain Java and should not depend on picocli, Spring Boot web APIs, or CLI configuration concepts.
The CLI and webapp should eventually depend on the core library rather than shaping its domain model.

## Goals / Non-Goals

**Goals:**

- Introduce a core library API for comparing one named SQL Server table between two JDBC connections.
- Represent tables, metadata, business keys, options, row keys, differences, and results with explicit Java types.
- Discover the target table's business key from a unique SQL Server index whose name ends with `_BK`.
- Support composite business-key indexes in the model even though the first fixture uses one column.
- Ignore default technical columns named `id` and `version` for the first comparison slice.
- Return a structured result that later CLI and web layers can consume.
- Provide deterministic text rendering for approval-based characterization tests.

**Non-Goals:**

- Do not add a CLI command.
- Do not add a webapp endpoint or UI.
- Do not compare multiple tables in one request.
- Do not compare schemas.
- Do not support non-SQL Server databases.
- Do not add external configuration files.
- Do not make the core library depend on Spring-specific or picocli-specific APIs.

## Decisions

### Build the core as plain Java over JDBC connections

The first public comparison API should accept left and right JDBC `Connection` instances plus a table comparison request.
This keeps the core library independent of CLI argument parsing, web request handling, Spring configuration, and connection lifecycle decisions.
Callers remain responsible for opening and closing connections.

Alternative considered: accept JDBC URLs and credentials directly.
This was rejected because that shape is CLI-friendly but too infrastructural for a reusable library API.

Alternative considered: accept Spring `DataSource` or `JdbcTemplate` only.
This was rejected because it would make the core library unnecessarily Spring-shaped before the CLI and web layers exist.

### Center the first slice on single-table comparison

The primary unit should be a single table reference such as schema `dbo` and table `PurchaseOrder`.
Future multi-table comparison can orchestrate repeated single-table comparisons and aggregate their results.
This avoids making discovery, ordering, filtering, and aggregate reporting part of the first library slice.

Alternative considered: scan and compare every table with a `_BK` index.
This was rejected because it would mix table discovery and orchestration with the first comparison behavior.

### Use typed domain models for result data

The core should expose structured comparison results rather than only rendered text.
The result model should include the table, business key, compared columns, ignored columns, rows only in left, rows only in right, and rows with per-column differences.
This lets later CLI and webapp layers render the same comparison result differently.

Alternative considered: return only a string report.
This was rejected because it would make the CLI-style output format the only library contract.

### Discover business keys from unique BK-suffixed indexes

For the target table, SQL Server metadata discovery should find a unique index whose name ends with the configured business-key suffix, defaulting to `_BK`.
The key columns of that index should define row identity for comparison.
The model should allow more than one key column, even if the current `PurchaseOrder_BK` fixture has only `reference`.

Alternative considered: infer the business key from column names ending `_BK`.
This was rejected because the established fixture uses natural domain column names and marks business-key role through index metadata.

### Partition columns into key, ignored, and compared columns

Business-key columns should identify rows and should not be reported as changed values.
Ignored columns should be excluded from both row matching and value comparison.
All remaining target-table columns should be compared.
The default ignored column names should be `id` and `version` for this first slice.

Alternative considered: ignore all identity columns and all `DATETIME2` columns automatically.
This was rejected because `DATETIME2` can represent meaningful business values, and identity handling should not accidentally hide non-standard schemas without an explicit rule.

### Render deterministic text as an adapter over structured results

A text report renderer should consume the structured result and produce stable output for Approval tests.
The report should expose the table, business-key index and columns, rows only on each side, differing rows, compared columns, and ignored columns.
This renderer is useful for tests and future CLI output, but it should not replace the structured result model.

## Risks / Trade-offs

- The first public API may need adjustment when `DataSource` support is added → Keep the initial API small and add overloads later rather than baking connection creation into the core.
- SQL Server identifier quoting can be error-prone → Represent table and column names as values and quote generated SQL identifiers centrally.
- Multiple `_BK` indexes on one table would be ambiguous → Fail clearly rather than choosing one arbitrarily.
- Missing `_BK` indexes would prevent row comparison → Fail clearly with a metadata error that names the table.
- Comparing SQL values can expose formatting and precision differences → Keep structured values separate from text rendering and make the renderer deterministic.
- Default ignored column names may be too opinionated later → Encapsulate them in comparison options so callers can override in a future extension.
