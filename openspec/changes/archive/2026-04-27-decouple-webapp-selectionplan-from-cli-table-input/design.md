## Context

The webapp scaffold currently models table selection as configuration fields that mirror CLI table options.
This couples web behavior to command-line input structure and limits future UI-driven automation.
The project needs a stable boundary where execution configuration can stay shared while selection strategy can evolve independently.

## Goals / Non-Goals

**Goals:**
- Introduce a `SelectionPlan` abstraction that resolves comparison targets as `List<TableRef>`.
- Keep shared execution configuration parity for connection, env-file, and output settings across CLI and webapp.
- Decouple webapp table-selection mechanics from CLI table flags and table-file semantics.
- Provide an initial explicit selection-plan implementation for concrete table lists.

**Non-Goals:**
- Do not change CLI table argument parsing or CLI validation behavior.
- Do not implement full automatic discovery logic in this change.
- Do not redesign comparison engine internals beyond consuming resolved table lists.

## Decisions

Use a strategy interface named `SelectionPlan` in the webapp flow.
This interface returns `List<TableRef>` and isolates table determination from input source details.
Alternative considered was continuing to bind web table data directly from configuration properties, but that would preserve CLI coupling.

Keep execution configuration as shared semantics while making selection channel-specific.
Connection, env-file, and output options remain aligned conceptually across CLI and webapp.
Table selection becomes an independent concern with its own extensibility points.
Alternative considered was one unified config object for all fields including table selectors, but it mixes stable execution settings with rapidly evolving UI behavior.

Provide an initial explicit concrete implementation for `SelectionPlan`.
The first implementation accepts concrete `TableRef` values and returns them deterministically.
Alternative considered was jumping directly to auto-discovery, but that introduces metadata dependencies and additional product decisions before the abstraction settles.

## Risks / Trade-offs

[Risk] Temporary duplication can exist while both config-based table fields and selection-plan wiring coexist during migration.
→ Mitigation is to define a clear deprecation path in the same change tasks and enforce tests on the new path.

[Risk] Different selection strategies may produce non-deterministic ordering.
→ Mitigation is to require deterministic `List<TableRef>` output and add tests for order stability.

[Risk] Future automated plans may need additional context beyond current configuration data.
→ Mitigation is to keep `SelectionPlan` contract small now and add context parameters only when concrete use-cases appear.

## Migration Plan

Add the `SelectionPlan` interface and explicit concrete implementation in the webapp module.
Update webapp execution wiring to consume `SelectionPlan` output instead of CLI-style table-selection fields.
Adjust docs and specs so web table-selection behavior is described as strategy-driven.
Add tests for explicit plan behavior and integration wiring.

## Open Questions

Should `SelectionPlan` return only `List<TableRef>` or a richer result with trace metadata for UI explanations.
Should UI manual selection and automated discovery be represented as separate plan types or a composite plan.
