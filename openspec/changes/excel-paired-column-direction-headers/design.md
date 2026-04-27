## Context

The Excel detail worksheet currently renders one header row for data columns.
Each paired column is labelled with `<column> (left)` and `<column> (right)`.
Users want a cleaner visual grouping that highlights a logical column once and shows side direction in a subordinate row.
The existing sheet already uses paired value columns, freeze panes, and deterministic ordering that must be preserved.

## Goals / Non-Goals

**Goals:**

- Replace per-cell `(left)/(right)` labels with a two-row header layout for paired columns.
- Keep the first column (`Result`) as a single header cell spanning the two header rows.
- Render each logical column name once across two adjacent cells.
- Render directional subheaders as `<<<` (left) and `>>>` (right).
- Preserve current row data population, styling semantics, and deterministic sheet structure.

**Non-Goals:**

- No change to comparison logic or output content values.
- No change to table-of-contents sheet layout.
- No change to colour-coding rules for difference rows.
- No change to output file format or request contract.

## Decisions

- Use two physical header rows in the detail table region.
  This keeps Excel-native filtering/inspection behavior simple and avoids rich-text header hacks.

- Merge cells for each logical column in the top header row.
  This visually groups paired columns and avoids repeating the column name.

- Keep directional sublabels as literal markers `<<<` and `>>>` in the second header row.
  Alternative considered: `L` and `R`.
  The arrow markers are more visually distinct in dense sheets.

- Keep `Result` as a merged cell spanning both header rows.
  This keeps the left-most header visually aligned with grouped paired headers.

- Update freeze-pane anchor to remain below the second header row and after the business-key paired columns.
  This preserves current navigation intent after adding an extra header line.

## Risks / Trade-offs

- [Risk] Merged header cells can complicate test assertions and column indexing.
  → Mitigation: add explicit tests for merged regions and exact header cell text.

- [Risk] Adding a second header row can shift row indexes in existing tests.
  → Mitigation: update tests to assert semantic positions rather than brittle magic indexes where practical.

- [Risk] Visual compactness could reduce clarity for users unfamiliar with markers.
  → Mitigation: use unambiguous `<<<` / `>>>` markers consistently for every paired column.
