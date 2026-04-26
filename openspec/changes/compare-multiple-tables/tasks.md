## 1. Fixture setup

- [ ] 1.1 Add a `supplier` fixture directory with schema and deterministic left/right data.
- [ ] 1.2 Define `dbo.Supplier` with an identity `id`, natural business-key column, `_BK` unique index, `version DATETIME2(3)`, and comparable domain columns.
- [ ] 1.3 Add a `product` fixture directory with schema and deterministic left/right data.
- [ ] 1.4 Define `dbo.Product` with an identity `id`, natural business-key column, `_BK` unique index, `version DATETIME2(3)`, and comparable domain columns.
- [ ] 1.5 Keep the existing `purchase-order` fixture as the third good comparable table.

## 2. Multi-table API model

- [ ] 2.1 Add a request type for comparing a non-empty ordered collection of table references.
- [ ] 2.2 Add a structured result type that contains one table comparison result per requested table.
- [ ] 2.3 Validate that empty table collections fail clearly.

## 3. Multi-table comparison service

- [ ] 3.1 Add a Spring-managed multi-table comparer service.
- [ ] 3.2 Delegate each requested table to the existing single-table comparer.
- [ ] 3.3 Preserve requested table order in the structured result.
- [ ] 3.4 Ensure unrequested comparable tables are not compared.
- [ ] 3.5 Let table-level metadata errors identify the failing table clearly.

## 4. Multi-table report rendering

- [ ] 4.1 Add deterministic text rendering for multi-table comparison results.
- [ ] 4.2 Render one section per table in result order.
- [ ] 4.3 Include each table's single-table comparison details in its section.

## 5. Integration tests

- [ ] 5.1 Initialize three good comparable fixtures in both logical databases.
- [ ] 5.2 Compare two selected tables using the multi-table API.
- [ ] 5.3 Verify the multi-table result contains exactly the selected two tables in request order.
- [ ] 5.4 Verify the third initialized good table is not present in the result.
- [ ] 5.5 Add an Approval test for the deterministic multi-table report.

## 6. Validation

- [ ] 6.1 Run the relevant unit and integration tests.
- [ ] 6.2 Run OpenSpec validation for the change.
