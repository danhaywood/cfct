## Context

The webapp currently binds default Enter activation to Compare when compare is enabled.
This can trigger a full comparison while the user is still preparing command selections.
The requested behavior is to make Refresh the default Enter-triggered action while preserving direct Compare execution on explicit user intent.

## Goals / Non-Goals

**Goals:**
- Make Refresh the default action for Enter in the selection workflow.
- Keep Compare available through explicit Compare button activation.
- Keep button enabled-state rules intact so disabled actions are never executed by keyboard activation.

**Non-Goals:**
- Redesign the overall selection workflow layout.
- Change comparison orchestration internals beyond action routing.
- Introduce new keyboard shortcuts beyond updating Enter default behavior.

## Decisions

Refresh becomes the default submit action in the selection workflow container.
Enter key handling routes to the same Refresh command path used by the Refresh button click.
Compare remains callable only from explicit Compare button activation to reduce accidental runs.
Existing enablement guards are reused so Enter does nothing when Refresh is disabled.
Tests that currently assert Enter-triggered Compare are updated to assert Enter-triggered Refresh.

## Risks / Trade-offs

[Users expecting old Enter behavior] → Provide clear default button emphasis on Refresh and align tests with the new contract.
[Potential mismatch between keyboard and click paths] → Reuse existing Refresh click orchestration path for Enter activation.
[Regression in disabled-state handling] → Add or update tests for disabled Refresh behavior under Enter.
