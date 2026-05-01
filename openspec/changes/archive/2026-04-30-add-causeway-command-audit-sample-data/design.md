## Context

The SQL Server harness already provisions Causeway command-log and audit-trail tables.
The next increment is realistic sample rows that connect command activity to audited table changes.
This supports upcoming auto-selection work that infers table footprints from command/audit history.
The project already has business fixture tables such as `Supplier` and `Product`.

## Goals / Non-Goals

**Goals:**
- Add deterministic sample command rows and matching audit rows.
- Enforce FK integrity from audit `interactionId` to command `interactionId`.
- Include a `registerProduct` command targeting a `Supplier` and generating `Product` audit events.
- Verify these fixtures via integration tests in both logical databases.

**Non-Goals:**
- Implement auto-selection query logic.
- Implement logical-identifier to table-entity mapping.
- Add broad command/audit history beyond targeted representative rows.

## Decisions

Add sample rows through existing fixture data SQL so data remains test-readable and easy to evolve.
Keep IDs deterministic using fixed GUID literals.

Represent command intent using `logicalMemberIdentifier` values such as `supplier.Supplier#registerProduct` and target values such as `supplier.Supplier:<id>`.
Represent resulting object footprint in audit rows with target values such as `product.Product:<id>` and key property changes.

Add the FK constraint in schema SQL so referential assumptions are validated by the database itself.
Insert command rows before audit rows in fixture load order.

Use integration assertions for row presence, FK viability, and specific command/audit linkage.

## Risks / Trade-offs

[Risk: Sample data could become brittle if business fixture IDs change] → Mitigation: use fixed, explicit IDs in SQL and keep test assertions aligned to those constants.
[Risk: FK addition could break existing fixture reset ordering] → Mitigation: keep drop/create ordering explicit and insert command rows before audit rows.
[Risk: Sample data may underrepresent future real-world audit variety] → Mitigation: treat this as minimal seed data and add broader cases in follow-on changes.
