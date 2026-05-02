## 1. Required Object Validation Rules

- [ ] 1.1 Add a centralized required target-object definition for `causewayExtCommandLog.CommandLogEntry`, `causewayExtAuditTrail.AuditTrailEntry`, and `util.LogicalTypeTableMapping`.
- [ ] 1.2 Implement target-database required-object lookup using `INFORMATION_SCHEMA.TABLES` with `TABLE_TYPE IN ('BASE TABLE', 'VIEW')`.
- [ ] 1.3 Return missing required objects in deterministic sorted schema-qualified format for error reporting.

## 2. Login and Connectivity Validation Integration

- [ ] 2.1 Integrate required-object validation into the existing login/connectivity validation flow after connectivity and database existence checks.
- [ ] 2.2 Ensure authentication/session creation is blocked when required target objects are missing.
- [ ] 2.3 Map missing-object failures to clear user-facing diagnostics that include missing object names.

## 3. Automated Test Coverage

- [ ] 3.1 Add or update unit tests for INFORMATION_SCHEMA-based required-object detection, including table-backed and view-backed success cases.
- [ ] 3.2 Add or update validation-service tests for login failure when one or more required target objects are missing.
- [ ] 3.3 Add or update webapp integration or browser-level tests to assert clear failure messaging for missing required target objects.

## 4. Fixture Script Invalid-Target Mode

- [ ] 4.1 Add a fixture-script flag for invalid-target mode and ensure normal mode behavior is unchanged.
- [ ] 4.2 Ensure invalid-target mode reports a deterministic non-existent target database name for manual testing.
- [ ] 4.3 Add or update script-level checks for start/restart flag parsing and invalid-target behavior.

## 5. Verification and Documentation

- [ ] 5.1 Run relevant webapp unit and integration/browser tests covering login and connectivity validation behavior.
- [ ] 5.2 Run fixture-script manual checks for both normal and invalid-target modes.
- [ ] 5.3 Update README or user notes to describe required target system objects, table-or-view acceptance, and invalid-target fixture usage.
