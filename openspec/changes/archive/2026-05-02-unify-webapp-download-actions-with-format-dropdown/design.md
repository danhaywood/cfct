## Context

The webapp currently renders separate JSON and Excel download actions in the comparison results stage.
The requested enhancement adds YAML download support and simplifies user choice through one download action paired with a format selector.
The existing backend already provides JSON and Excel payloads for the latest run, and YAML rendering capability exists in the core formatter layer.

## Goals / Non-Goals

**Goals:**
- Add YAML download support to the webapp results stage.
- Replace multiple download buttons with one download action plus format dropdown.
- Default the selected format to JSON.
- Keep the control deterministic and testable.

**Non-Goals:**
- Changing core comparison semantics is out of scope.
- Adding new file formats beyond JSON/YAML/Excel is out of scope.
- Reworking the overall results-stage layout outside download controls is out of scope.

## Decisions

Extend webapp comparison execution outcome to include YAML payload generated from the existing formatter.
Replace the separate anchors/buttons with a format selector component and a single download trigger.
On format change, rebind a single download resource to the selected payload and extension.
Set JSON as initial selected format and initial download target.
Retain stable `data-testid` attributes for selector and unified download action.

## Risks / Trade-offs

[Single control hides format options] → Keep format selector visible adjacent to the unified download action.
[Resource update bugs on selection change] → Add tests that assert filename/format switching behavior.
[Test churn from selector migration] → Update tests from multiple button expectations to unified-control expectations.
