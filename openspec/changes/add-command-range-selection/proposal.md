## Why

Selecting many commands one by one in the command grid is slow and error-prone when users need a contiguous batch.
Users need a fast range-selection interaction so they can mark large command spans without repetitive clicking.

## What Changes

- Add contiguous range selection in the command grid using a sensible multi-select idiom.
- Support anchoring on an initial selected row and extending selection to another row using Shift-click.
- Support keyboard-assisted range selection so users can complete range selection without pointer-only workflows.
- Preserve existing single-row toggle behavior and current keyboard navigation semantics.
- Keep downstream table auto-selection behavior driven by the resulting full selected-command set.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Extend command-row selection requirements to include contiguous range selection with anchor-and-extend behavior.
- `webapp-command-grid-keyboard-interaction`: Extend keyboard interaction requirements to cover keyboard-assisted range selection flows.

## Impact

This change affects Vaadin command-grid selection handling and selection-state orchestration in the webapp module.
This change affects Playwright and unit tests that assert command-selection interaction behavior.
No external API or dependency changes are expected.
