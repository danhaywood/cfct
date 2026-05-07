## Why

Users need a quick way to focus on recently executed commands without mentally scanning the full command history.
A baseline timestamp anchored above the command selection grid lets users define a cutoff point and immediately reduce noise during comparison setup.

## What Changes

- Add an optional baseline timestamp field above the command selection grid in the web application.
- Allow users to edit the baseline timestamp directly in that field.
- Add a context menu action on the command selection grid to set the baseline from the selected command's timestamp.
- Filter the command list so only commands with timestamps strictly after the baseline are displayed when a baseline is set.
- Keep existing behavior unchanged when no baseline is set.

## Capabilities

### New Capabilities
- `webapp-command-baseline-filter`: Baseline timestamp entry and baseline-driven filtering for the command selection grid.

### Modified Capabilities
- `webapp-command-selection-grid`: Add baseline awareness so displayed rows can be constrained by a user-selected cutoff timestamp.

## Impact

The main impact is in the web UI view model and Vaadin components that render and refresh the command selection grid.
The command-loading/filtering flow for the grid will be updated to apply the baseline cutoff when present while preserving current defaults when absent.
No external API contract changes are expected.
