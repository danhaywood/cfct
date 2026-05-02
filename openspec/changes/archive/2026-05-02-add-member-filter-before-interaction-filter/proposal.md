## Why

Users currently can filter commands by interaction ID only.
Finding commands by member ID requires scanning rows manually.
Placing member filtering first improves discoverability and matches common query flow.

## What Changes

Add a member ID filter control to the command selection section.
Place the member ID filter before the interaction ID filter.
Apply both filters together so command rows match the active filter criteria.
Keep existing command selection and command-driven table selection behavior unchanged.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-command-selection-grid`: Add member ID filter support and update filter control ordering.

## Impact

This change affects command-grid filtering UI and filter predicate logic in `MainView` and command selection state.
Unit and browser tests will need updates for filter ordering and member-based filtering behavior.
