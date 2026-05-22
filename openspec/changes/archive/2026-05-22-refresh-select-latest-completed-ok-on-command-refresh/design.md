## Context
The refresh action reloads command rows and currently clears all command selections.
Users then need extra interaction to choose a useful row before proceeding.

## Goals / Non-Goals
**Goals:**
Auto-select one refreshed command row when a suitable successful command exists.
Use deterministic selection logic based on the newest successful command.
Preserve keyboard flow by keeping focus on the selected row.

**Non-Goals:**
Changing compare execution semantics.
Changing filter behavior or baseline semantics.
Inferring success from fields other than replay state `OK`.

## Decisions
After refresh, evaluate visible command entries in current default sort order context.
Select the row with replay state `OK` and the greatest timestamp.
Clear any prior command selection before applying auto-selection.
Set `focusedCommandInteractionId` to the auto-selected row interaction id.
Trigger existing selection-to-table propagation path exactly once.
If no `OK` row exists, keep selection empty and preserve existing no-selection behavior.

## Risks / Trade-offs
If users expect a fully cleared state after refresh, auto-selection changes that expectation.
Mitigate by restricting auto-selection to clearly successful `OK` commands.

## Validation
Add or update MainView tests for refresh selecting latest `OK` row.
Add or update tests that ensure focus remains on the selected row.
Re-run targeted command-selection tests.
