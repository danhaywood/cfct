## 1. Add deployer-focused README prerequisites section

- [x] 1.1 Locate the README setup area where deployers prepare fixture and webapp/database inputs.
- [x] 1.2 Add a short "Database requirements" subsection that lists `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping` as required target-database table-or-view objects.
- [x] 1.3 Add guidance that compared business tables must provide a unique index or unique constraint whose name ends with `_PK`.

## 2. Add YAML configuration reference

- [x] 2.1 Add a README section with a copyable `application.yml` example that includes supported `cfct.webapp.comparison.*` and `cfct.webapp.selection-plan.explicit.tables` properties.
- [x] 2.2 Ensure YAML values use current defaults or explicit placeholders that are safe for deployers to edit.

## 3. Verify wording and consistency

- [x] 3.1 Confirm the new README wording matches existing behavior in current specs and does not introduce new runtime rules.
- [x] 3.2 Confirm the new sections are concise and placed before execution steps so prerequisites are visible before users run commands.

## 4. Expand deployer detail depth

- [x] 4.1 Add system-object structure details (column names and SQL Server types) for required target objects.
- [x] 4.2 Add a configuration property table that explains each supported key, default value, and purpose.
