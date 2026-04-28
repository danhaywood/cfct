## Context

Single-table comparison currently excludes configured ignored columns such as `version`, but still compares many technical identifier columns.
Technical identifiers like identity values, `guid`/`uuid` named columns, and SQL Server `UNIQUEIDENTIFIER` values often differ across environments while representing non-business variance.
This causes false-positive row differences and reduces signal quality in CLI, webapp, and exported reports.
The change affects core metadata/partition behavior and fixture characterization expectations.

## Goals / Non-Goals

**Goals:**
- Ensure identity-backed columns are always excluded from compared-value columns.
- Ensure columns named `uuid` or `guid` are always excluded from compared-value columns.
- Ensure columns with SQL Server datatype `UNIQUEIDENTIFIER` are always excluded from compared-value columns.
- Preserve business-key discovery and row matching behavior using configured PK-suffixed unique indexes.
- Keep deterministic outputs while reducing technical-column difference noise.

**Non-Goals:**
- No changes to business-key index suffix conventions.
- No changes to CLI/webapp request surfaces beyond behavior resulting from new defaults.
- No attempt to introduce per-table override policies in this change.

## Decisions

- Treat identity and GUID exclusions as built-in ignore rules applied after metadata discovery and before compared-column derivation.
  - Rationale: prevents technical columns from leaking into compared-column lists regardless of caller ignore options.
- Keep business-key columns valid for row matching even when those key columns are excluded from compared values.
  - Rationale: row identity and row value comparison serve different purposes and must be decoupled.
- Detect SQL Server `UNIQUEIDENTIFIER` using metadata type information rather than name heuristics alone.
  - Rationale: datatype-based exclusion is explicit and reliable across naming styles.
- Keep `uuid`/`guid` name matching case-insensitive and exact on normalized column names.
  - Rationale: avoids accidental misses while remaining predictable.

## Risks / Trade-offs

- [Risk] Existing tests that expect identity-only differences will fail.
  → Mitigation: update fixture characterization scenarios to reflect ignored technical columns.
- [Risk] Some consumers may rely on identity differences being reported.
  → Mitigation: document behavior change clearly in specs and release notes.
- [Risk] Metadata type detection might vary by JDBC driver behavior.
  → Mitigation: characterize against current SQL Server driver metadata and add targeted tests.

## Migration Plan

- Update core column partitioning implementation and metadata usage.
- Add/update tests for identity, `guid`/`uuid`, and `UNIQUEIDENTIFIER` exclusion behavior.
- Update fixture-based expectations where identity-only differences were previously reported.
- Validate existing comparison paths (single-table and multi-table) for deterministic behavior.

## Open Questions

- Should name-based exclusion include suffix/prefix patterns (for example, `supplier_guid`) or remain exact-name only in this change.
