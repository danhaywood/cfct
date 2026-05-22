## Context

The webapp uses a fixed footer for connection and comparison status information.
The comparison stage on the right side hosts tabs and per-table result grids.
Current sizing leaves unused vertical space in some viewport states, so table content does not reach the full available depth.
The requested behavior is to maximize result-table height while preserving clear separation from the footer.

## Goals / Non-Goals

**Goals:**
- Expand comparison result-grid viewport height to fill available space in the right-side content area.
- Guarantee footer non-overlap by reserving deterministic bottom clearance for the fixed footer.
- Keep results controls and tab interactions unchanged while improving visible row capacity.

**Non-Goals:**
- No changes to comparison semantics, row rendering, or diff classification.
- No redesign of the fixed footer content or placement.
- No introduction of floating or detached result controls.

## Decisions

Use a container layout strategy where comparison-stage wrappers and active tab content participate in a full-height flex chain.
This ensures the grid can inherit and consume remaining vertical space instead of collapsing to content-driven height.

Apply explicit bottom padding or inset accounting in the comparison-stage container equal to footer clearance requirements.
This prevents result-grid scroll areas from being obscured by the fixed footer.

Keep vertical scrolling inside the result-grid region rather than at page level for compared-row exploration.
This preserves interaction expectations and avoids shifting global page scroll behavior.

## Risks / Trade-offs

[Height propagation breaks at an intermediate container] → Add deterministic height/flex settings on each parent in the comparison-stage chain and verify with UI tests.
[Footer overlap regressions at smaller viewport heights] → Keep explicit reserved bottom clearance and verify with viewport resize scenarios.
[Unexpected impact on tab header/control rows] → Maintain fixed sizing for controls and allocate remaining space only to the grid content region.

## Migration Plan

Implement layout/style updates in comparison-stage and result-grid container components.
Update tests to assert that result-grid bottoms stay above the footer while using additional vertical space.
Run targeted webapp unit and browser tests to validate no regression in tabs, filters, and scrolling behavior.

## Open Questions

Should footer-clearance spacing be computed dynamically from rendered footer height or remain a stable fixed design token.
Should we tune minimum grid height for very short viewport scenarios to keep tab and filter controls usable.
