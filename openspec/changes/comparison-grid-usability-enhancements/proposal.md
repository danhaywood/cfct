## Why

The comparison workspace currently makes it harder to focus on actionable differences because matched rows are shown by default and controls are not optimized for quick analysis.
Users also report friction in navigation and execution because the Compare action lacks visual prominence and the business table selection grid can overlap nearby controls.

## What Changes

- Change the right-hand comparison grid to hide MATCH rows by default and add a user-controlled checkbox to show MATCH rows on demand.
- Add sortable columns and value filtering to the comparison results grid so users can quickly organize and narrow result sets.
- Restyle the Compare button so it is visually prominent as the primary action and adjust layout spacing so the business table selection grid never overlaps it.
- Add optional end-user resizing for the navigation drawer width while preserving sensible minimum and maximum bounds.

## Capabilities

### New Capabilities
- `webapp-resizable-navigation-drawer`: Allow end-users to resize the navigation drawer width within configured limits and persist the chosen width for the session.

### Modified Capabilities
- `webapp-comparison-results-tabs`: Update result-grid behavior to hide MATCH rows by default, support explicit MATCH row toggling, and provide sorting and value filtering.
- `webapp-command-selection-grid`: Ensure command-area layout keeps the Compare primary action unobstructed and non-overlapping with the business table selection grid.
- `webapp-main-ui-layout`: Extend shell layout behavior to accommodate a user-resizable navigation drawer without breaking responsive behavior.

## Impact

Affected areas include Vaadin comparison result views, command and selection grid layout components, and main application shell styling and interaction logic.
No external API contract changes are expected, and dependencies should remain unchanged unless a Vaadin add-on is needed for drawer resize handles.