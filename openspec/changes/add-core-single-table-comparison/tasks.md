## 1. Core API model

- [ ] 1.1 Add plain Java value types for table references, column references, business keys, comparison options, and table comparison requests.
- [ ] 1.2 Add structured result types for table comparison results, row keys, rows only on one side, row differences, and column differences.
- [ ] 1.3 Keep the core API independent of CLI, web request, Spring MVC, and picocli types while allowing Spring-managed services or configuration.

## 2. SQL Server metadata discovery

- [ ] 2.1 Implement SQL Server metadata reading for the target table's columns and identity metadata.
- [ ] 2.2 Implement discovery of exactly one unique index whose name ends with the configured business-key suffix.
- [ ] 2.3 Preserve business-key column ordinal order and support more than one key column in the model.
- [ ] 2.4 Fail clearly when the target table has no matching business-key index.
- [ ] 2.5 Fail clearly when the target table has multiple matching business-key indexes.
- [ ] 2.6 Partition table columns into key columns, ignored columns, and compared columns using default ignored names `id` and `version`.

## 3. Row reading and comparison engine

- [ ] 3.1 Implement safe SQL Server identifier quoting for generated table and column SQL.
- [ ] 3.2 Read target-table rows from left and right JDBC connections using business-key and compared columns.
- [ ] 3.3 Match rows by business-key values.
- [ ] 3.4 Record rows only in left and rows only in right.
- [ ] 3.5 Compare matched rows across non-key non-ignored columns and record per-column differences.
- [ ] 3.6 Ensure rows that differ only in ignored columns do not produce row differences.

## 4. Report rendering

- [ ] 4.1 Add a deterministic text renderer for structured table comparison results.
- [ ] 4.2 Include table name, business-key index and columns, compared columns, and ignored columns in the report.
- [ ] 4.3 Render left-only rows, right-only rows, and differing rows in deterministic order.
- [ ] 4.4 Render a deterministic no-differences indication for empty comparison results.

## 5. Integration tests with PurchaseOrder fixture

- [ ] 5.1 Add integration test setup that initializes the existing realistic `PurchaseOrder` fixture in both logical databases.
- [ ] 5.2 Verify that comparing `dbo.PurchaseOrder` uses `PurchaseOrder_BK(reference)` as the business key.
- [ ] 5.3 Verify that `id` and `version` are ignored by default.
- [ ] 5.4 Verify that the expected left-only row, right-only row, and domain value differences are reported.
- [ ] 5.5 Add an Approval test for the deterministic text report.
- [ ] 5.6 Add focused tests for missing and ambiguous `_BK` index metadata failures.

## 6. Validation

- [ ] 6.1 Run the relevant unit and integration tests.
- [ ] 6.2 Run OpenSpec validation for the change.
