## Context

The SQL Server test harness now provisions left and right logical databases and can initialize them independently from SQL resources.
The current fixture is deliberately minimal and only proves that independent setup works.
The next useful foundation is a realistic table fixture that captures expected row-comparison conventions without implementing comparison behavior yet.

Future data comparison is expected to match rows using business keys rather than SQL Server identity values.
For this fixture, the business key will be represented by a unique index whose name ends with `_BK` and whose indexed column is the domain column `reference`.

## Goals / Non-Goals

**Goals:**

- Define a realistic `dbo.PurchaseOrder` schema that is identical in both logical databases.
- Include an `id` identity primary key as a surrogate database key.
- Include a `reference` domain column and a unique business-key index named with the `_BK` suffix.
- Include representative comparable purchase order columns.
- Include a `version` column using `DATETIME2`, not SQL Server `timestamp` or `rowversion`.
- Populate left and right databases with fixture rows that will be useful for future comparator tests.
- Prove that the fixture can be loaded and queried through the existing harness.

**Non-Goals:**

- Do not implement business-key index discovery.
- Do not implement row matching by business key.
- Do not implement data comparison or diff rendering.
- Do not implement schema comparison.
- Do not introduce migration tooling or production database setup.

## Decisions

### Add a separate realistic fixture rather than overloading the smoke fixture

The current `sample_items` fixture is useful as a tiny harness smoke test.
The `PurchaseOrder` fixture should be added as a domain-shaped fixture so future comparison tests can depend on it without making the smoke fixture carry product semantics.

Alternative considered: replace `sample_items` with `PurchaseOrder` everywhere.
This was rejected because it would blur harness verification with comparison-oriented fixture design.

### Use identical schemas in left and right databases

Both logical databases should receive the same `dbo.PurchaseOrder` schema.
This keeps the fixture focused on future row-level data comparison rather than schema comparison.

Alternative considered: introduce schema differences immediately.
This was rejected because it would mix two dimensions of comparison before the row-comparison fixture is established.

### Use an index name suffix to identify business keys

The fixture should create a unique index named with the `_BK` suffix, such as `PurchaseOrder_BK`, over the `reference` column.
This models the intended future convention that business-key join columns are discovered from indexes whose names end in `_BK`.

Alternative considered: name the business-key column `PurchaseOrder_BK`.
This was rejected because the domain column should keep its natural name, `reference`, and the business-key role belongs to metadata on the index.

### Use `DATETIME2` for the `version` column

The fixture should model `version` as a `DATETIME2` column that maps naturally to `java.sql.Timestamp` through JDBC.
It MUST NOT use SQL Server `timestamp` or `rowversion`, because those represent SQL Server-generated binary versioning rather than the application-managed timestamp intended here.

Alternative considered: use SQL Server `rowversion`.
This was rejected because it would imply database-generated binary concurrency semantics that are not wanted for this fixture.

### Seed rows that anticipate future comparison cases

The fixture data should include rows that are equal by domain values, rows with differing domain values, rows missing from one side, and rows whose only expected future-ignored differences are `id` and `version`.
This makes the fixture ready for later comparison work while keeping this change limited to setup and verification.

## Risks / Trade-offs

- Fixture data may look like comparator behavior even though no comparator exists yet → Keep tests limited to fixture loading and shape verification.
- The `_BK` convention may later need to support composite indexes → Use an index-based convention now so composite business keys remain possible later.
- `version` could be mistaken for SQL Server `rowversion` → Declare it explicitly as `DATETIME2` in the spec and fixture.
- Monetary and timestamp values can introduce precision surprises → Use explicit SQL Server types and deterministic literal values in fixture data.
