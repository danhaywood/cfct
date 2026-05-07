## Context

The Excel renderer currently writes differing values into one cell using inline `L:` and `R:` prefixes.
This compact format reduced column count but made spreadsheets harder to scan, filter, and visually compare.
The product requirement is to restore directional left/right columns in Excel while still collapsing columns that never differ.
The webapp presentation is out of scope, and only Excel output behavior changes.

## Goals / Non-Goals

**Goals:**
- Produce clearer Excel detail sheets by using separate left and right columns for logical fields that have at least one differing row.
- Keep logical fields with no differences across reported rows as one shared column to avoid unnecessary width growth.
- Always keep primary-key/business-key fields as one shared column in Excel detail sheets.
- Replace inline `L:` / `R:` value prefixes in Excel cells with directional headers (`<<<` and `>>>`).

**Non-Goals:**
- No changes to JSON or YAML output structures.
- No changes to webapp rendering semantics.
- No changes to table correlation rules or business-key matching logic.

## Decisions

### Decision: Compute directional layout per table before writing headers and rows
The renderer will pre-scan each table result to determine which logical fields require split directional columns.
A logical field is marked split when any differing row contains non-equal left and right values for that field.
A logical field remains shared when all reportable rows are equal for that field or only one side has values by row-type semantics.
This approach keeps header and row writing deterministic and avoids per-row shape changes.
Alternative considered was deciding split-vs-shared at row-write time, but that would produce unstable column positions and fragile formatting.

### Decision: Force key fields to shared mode
Primary-key/business-key columns will always render as one shared column regardless of difference detection results.
This aligns with correlation semantics and avoids redundant directional key columns.
Alternative considered was allowing keys to split for consistency, but that adds noise without additional diagnostic value.

### Decision: Direction is represented in headers, not cell payload text
For split fields, headers use paired `<<<` and `>>>` labels and raw cell values are written without inline side prefixes.
This enables native Excel filtering and copy/paste workflows without parsing embedded prefixes.
Alternative considered was retaining inline prefixes for backward familiarity, but it keeps the readability problem unresolved.

### Decision: Preserve existing row classification and coloring
Row-type labels (`Only in left`, `Only in right`, `Differ`) and existing color semantics remain unchanged.
Only column layout and per-cell value text representation change.
Alternative considered was introducing new row labels for split mode, but that is unnecessary and increases migration effort.

## Risks / Trade-offs

[Layout pre-scan increases renderer complexity] → Mitigation: isolate split-detection in a dedicated helper with focused unit tests.

[Large tables may incur extra processing due to pre-scan] → Mitigation: perform a single linear pass over reported rows and reuse computed layout metadata during write.

[Legacy tests may assert inline `L:` / `R:` payloads] → Mitigation: update golden assertions to validate header structure and raw cell values separately.

[Ambiguity around null vs blank equality handling] → Mitigation: reuse existing equality semantics from comparison result generation instead of adding renderer-specific comparisons.

## Migration Plan

Implement behind existing Excel renderer code path without feature flags because this is the intended replacement behavior.
Update or add automated tests that assert conditional split columns, key-column collapsing, and `<<<`/`>>>` headers.
If rollback is required, revert the renderer layout helper and associated tests in one change-set.

## Open Questions

None.
