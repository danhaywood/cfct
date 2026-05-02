## Context

The webapp currently validates SQL Server connectivity and database existence during login.
Command-driven workflows also depend on system support objects in the target database.
Missing support objects currently surface only after login during downstream command or audit interactions.
The target platform is Azure SQL Managed Instance or SQL Server 2022, so `INFORMATION_SCHEMA` coverage is sufficient for object presence checks.
The user explicitly accepts required objects being implemented as views instead of base tables.

## Goals / Non-Goals

**Goals:**
- Fail login-time validation when required target support objects are missing.
- Detect required objects with a single deterministic query pattern against `INFORMATION_SCHEMA` in the target database context.
- Accept either `BASE TABLE` or `VIEW` for each required object.
- Return actionable diagnostics that list exactly which required objects are missing.
- Provide a fixture-script flag that sets up a manual invalid-target-database test path without hand-editing SQL fixtures.

**Non-Goals:**
- Validate column-level schema, keys, constraints, or data quality of required objects.
- Validate source-database support objects in this change.
- Change command resolution or comparison logic beyond gating login on missing required objects.
- Add a dedicated test harness or new Docker image just for invalid-target testing.

## Decisions

- Add a required-object validation step to the existing login/connectivity validation flow after connection and database existence checks pass.
  - This keeps failure reporting ordered and avoids querying metadata on unreachable targets.
  - Alternative considered: defer checks until first command execution.
  - Deferred checks were rejected because they delay feedback and create runtime surprises.
- Define required objects as fully qualified pairs of schema and object name.
  - Initial required set is `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping`.
  - Alternative considered: infer required objects dynamically from feature toggles.
  - Dynamic inference was rejected because it complicates deterministic validation and test coverage.
- Query `INFORMATION_SCHEMA.TABLES` in the target database for required names and allow `TABLE_TYPE IN ('BASE TABLE', 'VIEW')`.
  - This directly satisfies Azure SQL MI and SQL Server 2022 compatibility and the view-allowed requirement.
  - Alternative considered: use `sys.objects` and `sys.schemas`.
  - `sys` catalog was rejected for this change because `INFORMATION_SCHEMA` is sufficient and requested.
- Produce a single validation error that includes missing object list in stable sorted order.
  - Stable ordering keeps unit and browser assertions deterministic.
  - Alternative considered: fail fast on first missing object.
  - Fail-fast was rejected because it hides full remediation scope.
- Extend `scripts/fixture-sqlserver.sh` with an explicit invalid-target mode flag.
  - Invalid-target mode leaves normal source fixture setup intact while intentionally using a non-existent target database name for manual login failure testing.
  - Alternative considered: require manual editing of webapp runtime args for every test.
  - Manual editing was rejected because it is error-prone and undocumented for repeatable QA checks.

## Risks / Trade-offs

- [False sense of readiness from object-only checks] → Document that this validation confirms presence only, not structural correctness.
- [Future required-object set drift] → Centralize required-object definitions in one constant and cover with focused tests.
- [Different permissions for metadata visibility] → Surface clear error messaging when metadata query cannot be executed.
