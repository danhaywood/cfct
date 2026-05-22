## Context

The command selection grid provides a baseline timestamp filter and a context-menu action to set baseline from a selected command row.
Current behavior filters to commands strictly after the baseline timestamp, which excludes the selected baseline command itself.
This makes the context-menu workflow feel inconsistent because the chosen command disappears immediately.

## Goals / Non-Goals

**Goals:**
- Include the selected baseline command in the filtered command grid after baseline is set from that command.
- Keep command filtering deterministic and easy to reason about.
- Maintain compatibility with existing member, interactionId, and replay-state filter composition.

**Non-Goals:**
- Redesigning baseline UI controls.
- Changing command catalog sorting defaults.
- Altering unrelated command selection or table-selection behavior.

## Decisions

Apply inclusive baseline comparison for baseline filter evaluation so rows at the baseline timestamp remain visible.
Preserve existing timestamp-ascending visible order, which naturally keeps the selected baseline command at the top when set from selected command in normal usage.
Keep baseline filter state and context-menu set-baseline workflow unchanged outside comparison semantics.
Update tests to assert selected command visibility and top-row placement after set-baseline action.

## Risks / Trade-offs

[Risk] Multiple commands with identical timestamps may appear at the baseline boundary. → Mitigation: rely on existing deterministic secondary ordering and assert selected command remains visible.
[Risk] Existing tests may assert strict-after semantics. → Mitigation: update tests and spec scenarios to inclusive behavior.
[Risk] Users may expect old strict behavior in edge cases. → Mitigation: align behavior with explicit context-menu intent of using selected command as visible baseline anchor.
