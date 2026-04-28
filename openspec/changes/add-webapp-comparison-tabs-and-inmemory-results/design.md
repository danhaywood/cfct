## Context

The current webapp selection stage enables `Compare` only as a placeholder interaction and does not run comparison orchestration.
Comparison libraries already perform deterministic table comparison logic and can be reused for webapp execution.
Current output pathways are oriented around rendered text/JSON/Excel delivery rather than direct UI consumption.
The requested behavior needs in-memory result delivery so Vaadin components can render per-table differences without file marshalling.
The change crosses API contracts, implementation wiring, and webapp UI rendering and therefore needs explicit design decisions.

## Goals / Non-Goals

**Goals:**
- Execute comparison for all currently selected eligible tables when the user clicks `Compare`.
- Return comparison results in memory from comparison orchestration contracts used by the webapp.
- Render one result tab per selected table in the right-side comparison area.
- Render per-table comparison rows in Vaadin grids with Excel-like visual structure.
- Preserve existing comparison semantics and datasource-managed connection lifecycle.

**Non-Goals:**
- No replacement of CLI report output formats or CLI invocation model.
- No redesign of table-selection eligibility rules.
- No requirement to match Excel export formatting pixel-for-pixel.

## Decisions

- Introduce or extend API-level result DTOs so multi-table comparison results can be consumed in memory by UI clients.
  - Rationale: UI rendering should consume domain result structures directly rather than parsing externalized report payloads.
  - Alternative considered: reuse marshalled JSON/text payloads and parse in webapp.
  - Rejected because it couples UI logic to report serialization and duplicates mapping logic.
- Keep datasource-based execution boundaries in webapp services and map internal comparison outputs to API DTOs within service layer.
  - Rationale: preserves lifecycle safety and module boundaries while enabling richer UI rendering.
  - Alternative considered: push Vaadin-specific models into implementation module.
  - Rejected because it violates API-first decoupling.
- Render result tabs using Vaadin `Tabs` plus content container keyed by table identity.
  - Rationale: tab-per-table aligns with selected-table workflow and scales to multi-table comparisons.
  - Alternative considered: a single combined grid with table grouping.
  - Rejected because per-table navigation is clearer and mirrors user intent from selection stage.
- Render per-table grid columns in paired left/right style with status/highlight cells inspired by Excel comparison layout.
  - Rationale: consistent mental model with existing spreadsheet output while staying idiomatic for web UI.
  - Alternative considered: plain unstyled JSON-like table view.
  - Rejected because it lowers readability for row-level differences.
- Remove non-informative right-side placeholder labels and replace them with action-oriented controls.
  - Rationale: users need comparison tools (filter/download) more than static explanatory text after compare execution is functional.
- Provide compared-table filtering and JSON/Excel download controls in the result stage.
  - Rationale: improves usability for many selected tables and keeps export workflows available from the webapp.
- Maintain asynchronous-safe UI updates by running comparison action in server-side click handling and replacing result container content atomically.
  - Rationale: avoids partial stale tabs after selection changes or repeated compare runs.

## Risks / Trade-offs

- [Risk] In-memory result payloads for many selected tables can increase heap usage.
  → Mitigation: scope payload to selected tables only and avoid retaining obsolete run results after refresh.
- [Risk] Mapping implementation outputs to API DTOs can duplicate fields if not normalized.
  → Mitigation: define one canonical API result model for webapp and future consumers.
- [Risk] Excel-like styling in Vaadin grids may drift from export conventions over time.
  → Mitigation: centralize style tokens and add browser assertions for key visual semantics.
- [Risk] Compare execution latency may block perceived UI responsiveness for larger datasets.
  → Mitigation: show deterministic loading state and disable repeated compare clicks during execution.

## Migration Plan

- Add or extend API result contracts for in-memory multi-table comparison output.
- Update implementation wiring to provide these results through existing API service boundaries.
- Update webapp comparison action flow to execute compare and bind tabbed grid results.
- Update Playwright and unit tests to assert compare execution, tab creation, and result-grid rendering.
- Validate existing CLI and integration behavior remains unchanged for externalized output workflows.

## Open Questions

- Should per-tab grids include expandable row-detail views for large difference payloads in this change or a follow-up.
- Should the initial selected tab follow selection order or a deterministic sorted order.
