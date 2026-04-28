## Context

Comparison output is currently rendered in paired left/right form in both the web result grid and Excel detail sheets.
This paired format is useful for differences but verbose for equal values, where both sides repeat the same content.
The requested change is presentation-focused and should not alter comparison detection, row status classification, or table selection flows.

## Goals / Non-Goals

**Goals:**
- Render equal compared values once in the web results grid.
- Keep `L:`/`R:` paired rendering for values that differ.
- Apply the same compact rendering rule in Excel detail sheets.
- Preserve deterministic output ordering and existing row-status semantics.
- Update screenshots and tests to reflect the new compact output.

**Non-Goals:**
- Change comparison algorithms, business-key rules, or difference classification logic.
- Change login, navigation, or compare execution orchestration.
- Change JSON output structure.

## Decisions

1. Add a presentation-layer value-shaping step that classifies each logical field as equal or different for display purposes.
Rationale: This isolates the change to rendering paths and avoids touching core comparison logic.
Alternative considered: Alter model types returned by comparison services.
Why not chosen: Model-shape changes would ripple through API and unrelated consumers.

2. In web tabs, render one column for fields that are equal across all displayed rows, and paired `L:`/`R:` columns for fields that contain at least one difference.
Rationale: Column-level compaction avoids per-row column jitter and keeps the grid stable.
Alternative considered: Per-row dynamic cell layout with variable column count.
Why not chosen: Variable row shape would reduce readability and complicate testing.

3. In Excel detail sheets, use a single merged-value cell for equal fields and retain paired directional cells only where differences exist.
Rationale: The spreadsheet should mirror the same information-density rule as the web grid.
Alternative considered: Keep fixed paired sheet structure and only blank duplicate equal cells.
Why not chosen: Blank duplicates still consume horizontal space and do not satisfy the compact-output goal.

4. Keep explicit direction markers and color coding for differing values unchanged.
Rationale: Existing difference inspection cues remain familiar and testable.
Alternative considered: Introduce a new color or marker scheme with compaction.
Why not chosen: Not required for this scope and increases migration risk.

## Risks / Trade-offs

- Compact columns may reduce immediate visibility of side provenance for equal values.
  → Mitigation: Keep `L:`/`R:` direction markers wherever values differ and preserve row status indicators.
- Excel layout changes can break existing downstream manual expectations.
  → Mitigation: Update README screenshots and add focused renderer assertions.
- Grid header expectations in tests may fail after compaction.
  → Mitigation: Update test suites to assert compact-vs-paired behavior explicitly.

## Migration Plan

1. Implement compact-value shaping for web grid column definitions and cell renderers.
2. Implement equivalent compact-value shaping in Excel detail-sheet writer logic.
3. Update unit and browser tests for compact and differing-field scenarios.
4. Regenerate and commit README screenshots showing compact output.

## Open Questions

No blocking open questions are identified.
