## Why

Selecting many tables one by one is slow and error-prone when users need to compare multiple related tables in a single run.
The table selection flow should support standard multi-select interactions and fast bulk selection to reduce setup time and improve usability.

## What Changes

- Add range-style and additive multi-selection support in the table selection list using `Shift+Space` and `Shift+Click`.
- Add a "Select all" checkbox that selects every selectable table while skipping disabled tables.
- Ensure disabled or non-selectable tables are never selected by keyboard, mouse, or bulk selection actions.
- Preserve clear and predictable selection behavior when users combine single-select, shift-select, and select-all actions.

## Capabilities

### New Capabilities
- `webapp-table-bulk-selection-controls`: Supports select-all behavior that respects disabled tables in table selection workflows.

### Modified Capabilities
- `webapp-manual-table-selection`: Expands selection interactions to include `Shift+Space` and `Shift+Click` for multi-table selection.

## Impact

This change affects the web application table selection UI, including selection state handling and keyboard and pointer event behavior.
It may require updates to Vaadin grid selection wiring, related view-model state logic, and UI tests that verify table selection behavior.
