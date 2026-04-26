## 1. Fixture setup

- [x] 1.1 Add a `supplier` fixture directory with schema and deterministic left/right data.
- [x] 1.2 Define `dbo.Supplier` with an identity `id`, natural business-key column, `_BK` unique index, `version DATETIME2(3)`, and comparable domain columns.
- [x] 1.3 Add a `product` fixture directory with schema and deterministic left/right data.
- [x] 1.4 Define `dbo.Product` with an identity `id`, natural business-key column, `_BK` unique index, `version DATETIME2(3)`, and comparable domain columns.
- [x] 1.5 Keep the existing `purchase-order` fixture as the third good comparable table.

## 2. Multi-table API model

- [x] 2.1 Add a request type for comparing a non-empty ordered collection of table references.
- [x] 2.2 Add a structured result type that contains one table comparison result per requested table.
- [x] 2.3 Validate that empty table collections fail clearly.

## 3. Multi-table comparison service

- [x] 3.1 Add a Spring-managed multi-table comparer service.
- [x] 3.2 Delegate each requested table to the existing single-table comparer.
- [x] 3.3 Preserve requested table order in the structured result.
- [x] 3.4 Ensure unrequested comparable tables are not compared.
- [x] 3.5 Let table-level metadata errors identify the failing table clearly.

## 4. Multi-table report rendering

- [x] 4.1 Add deterministic text rendering for multi-table comparison results.
- [x] 4.2 Render one section per table in result order.
- [x] 4.3 Include each table's single-table comparison details in its section.

## 5. Integration tests

- [x] 5.1 Initialize three good comparable fixtures in both logical databases.
- [x] 5.2 Compare two selected tables using the multi-table API.
- [x] 5.3 Verify the multi-table result contains exactly the selected two tables in request order.
- [x] 5.4 Verify the third initialized good table is not present in the result.
- [x] 5.5 Add an Approval test for the deterministic multi-table report.

## 6. Validation

- [x] 6.1 Run the relevant unit and integration tests.
- [x] 6.2 Run OpenSpec validation for the change.
