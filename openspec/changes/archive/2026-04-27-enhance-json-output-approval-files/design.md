## Context

The project already supports configured comparison requests that dispatch to JSON or Excel output.
The JSON renderer currently emits high-level table details, row keys for missing rows, and changed column values for differing rows.
The Excel renderer emits a richer inspection-oriented report with a table of contents, per-table counts, metadata, and one detail row per reportable difference with paired left and right values.
Integration tests already use ApprovalTests for text output and write an Excel workbook under `target/excel-comparison-output` for manual inspection.

## Goals / Non-Goals

**Goals:**

- Make JSON output a complete structured report that exposes the same kinds of inspection data as Excel output.
- Keep JSON deterministic by preserving table order, row order, compared column order, and stable object field order.
- Include per-table summary counts and row-level left/right values for rows only in left, rows only in right, and differing rows.
- Add a test that writes approval-style artifacts for JSON and Excel so reviewers can inspect current renderer output easily.

**Non-Goals:**

- Do not change comparison input JSON syntax.
- Do not change how rows and columns are compared.
- Do not replace the Excel workbook renderer.
- Do not require production code to understand ApprovalTests concepts.

## Decisions

- Extend the existing JSON renderer model rather than introducing a second JSON renderer.
  Alternative considered: add a separate detailed JSON output type.
  Reusing the existing renderer keeps the configured `json` output type as the canonical machine-readable report and avoids another request option.

- Represent missing rows as objects that include `key`, `leftValues`, and `rightValues` fields, with the absent side represented as an empty object or omitted consistently by the renderer implementation.
  Alternative considered: keep missing rows as key-only arrays.
  Including values matches the Excel detail rows and makes JSON sufficient for detailed inspection.

- Represent differing rows as objects that include `key`, `leftValues`, `rightValues`, and `differences`.
  Alternative considered: only emit changed columns.
  Including all displayed compared values lets JSON support the same side-by-side inspection as Excel while the `differences` list still identifies changed cells directly.

- Include a per-table `summary` object with counts for compared columns, ignored columns, rows only in left, rows only in right, differing rows, and whether the table has differences.
  Alternative considered: rely on consumers to count arrays.
  Explicit counts mirror the Excel Table of Contents and make the report easier to inspect and consume.

- Add approval-style test artifacts under a deterministic `target` directory.
  Alternative considered: commit binary Excel approvals.
  Writing generated files to `target` avoids committing binary churn while still making inspection straightforward after running tests.

## Risks / Trade-offs

- Existing JSON approval baselines will change → update approved files as part of implementation.
- Downstream consumers of the current JSON shape may need to adapt → keep old high-value fields where practical and document any structural additions clearly in tests.
- Full row values may make JSON output larger → acceptable because detailed inspection is the purpose of report output.
- Binary Excel output is not convenient for text approval diffs → write the workbook itself and optionally a text summary of workbook structure for approval-style review.
