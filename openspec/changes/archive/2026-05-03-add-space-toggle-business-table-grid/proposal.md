## Why

Keyboard users can toggle command-grid rows with Space, but the business table grid does not provide the same interaction.
This inconsistency slows keyboard-driven workflows and harms accessibility.

## What Changes

- Add Space-key selection toggling for the focused row in the manual business table grid.
- Keep arrow-key navigation behavior in the business table grid predictable and aligned with existing grid semantics.
- Ensure Space-key toggling uses the same manual table selection state path as mouse checkbox interaction.
- Add test coverage for business-grid Space toggle behavior.

## Capabilities

### New Capabilities
- `webapp-business-grid-keyboard-interaction`: Keyboard-first selection behavior for the manual business table grid.

### Modified Capabilities
- `webapp-manual-table-selection`: Extend grid interaction requirements to include Space-key selection toggle for focused business table rows.

## Impact

- Affected webapp UI event handling in manual table selection grid.
- Affected unit/browser tests for manual table selection interactions.
- No backend API or persistence changes.
