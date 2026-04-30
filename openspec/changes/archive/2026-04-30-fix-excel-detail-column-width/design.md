## Context

The project generates Excel workbooks to present comparison results, including one detail worksheet per compared table.
Detail-sheet formatting currently autosizes multiple columns, and this includes the second column.
Autosizing the second column creates avoidable visual variance and breaks alignment with adjacent value columns.
This change is limited to formatting behavior in the Excel detail worksheet writer.

## Goals / Non-Goals

**Goals:**
- Keep column 2 on detail sheets at a deterministic fixed width.
- Make column 2 width exactly match column 3 width.
- Preserve all existing detail-sheet content, semantics, and color coding.

**Non-Goals:**
- Redesigning the detail-sheet layout.
- Changing Table of Contents formatting.
- Changing which data appears in any column.

## Decisions

- Reuse the existing width computed or applied for column 3 as the source of truth for column 2 width.
This avoids introducing a new hard-coded width constant and guarantees visual parity between the two columns.
- Disable autosizing for column 2 in the detail-sheet autosize pass.
This ensures a later autosize step cannot override the explicit width choice.
- Keep autosizing behavior for all other columns unchanged.
This minimizes risk and preserves current readability behavior for wide data fields.

## Risks / Trade-offs

- [Risk] Column 3 width source changes in future refactors could indirectly alter column 2 width.
→ Mitigation: Add or update tests that assert column 2 equals column 3 width in generated detail sheets.
- [Trade-off] Fixed parity between columns 2 and 3 may not be ideal for every dataset.
→ Mitigation: Scope is intentional for cosmetic consistency, and future refinements can be proposed separately.
