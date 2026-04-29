## Context

The project already emits structured comparison output through library code paths and CLI-driven flows.
Current behavior supports JSON output selection and file writing but does not expose YAML as a first-class format.
The change spans at least core output serialization and CLI argument handling, so a shared design avoids divergence.

## Goals / Non-Goals

**Goals:**
Support YAML serialization for comparison results in library APIs and CLI execution paths.
Keep output-format selection consistent so JSON and YAML follow the same validation and writer dispatch patterns.
Preserve backward compatibility for existing JSON users and defaults.

**Non-Goals:**
Changing comparison result semantics or field naming.
Introducing YAML-only fields that differ from JSON output content.
Adding support for formats beyond YAML and JSON in this change.

## Decisions

Use a single output-format enum or discriminator shared by library and CLI modules.
This keeps argument parsing, writer resolution, and validation aligned and avoids stringly-typed branching.

Add a YAML serializer path that maps the same result model used by JSON output.
This ensures output parity across formats and reduces duplicate mapping logic.

Prefer an existing project serialization dependency if YAML support already exists in the dependency graph.
If absent, add a minimal and well-supported YAML dependency and isolate usage behind the output writer abstraction.

Retain default output format behavior as-is when no format is specified.
This prevents breaking changes and keeps existing scripts stable.

## Risks / Trade-offs

[Output parity drift between JSON and YAML] → Add tests that compare structure-level equivalence from the same comparison result fixture.
[Dependency footprint increase from YAML library] → Reuse existing libraries when possible and keep serialization integration isolated.
[CLI UX confusion about format-specific extensions] → Document `.yaml`/`.yml` output expectations and enforce extension handling in one place.

## Migration Plan

Implement format enum and writer dispatch changes behind existing call sites first.
Add YAML CLI argument acceptance and output file handling next.
Add tests for library serialization and CLI end-to-end output generation.
Update docs and examples after tests pass.
Rollback is low risk by removing YAML format registration and dependency if issues arise.

## Open Questions

Should CLI-generated YAML files default to `.yaml` specifically, or preserve user-provided extension exactly.
Should YAML output support both block and flow style configuration, or rely on serializer defaults for now.
