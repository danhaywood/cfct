## Context

The left drawer currently renders command controls, command grid, compare action bar, and business table controls in a top-first stack.
Recent changes increased drawer content height, making responsive behavior more sensitive on smaller viewports.
Users should be able to select business tables and access compare without losing the action off-screen.

## Goals / Non-Goals

**Goals:**
- Place the compare button below the business table grid.
- Keep the compare button visible in resized browser states.
- Preserve a visible spacer between command and business table sections.

**Non-Goals:**
- Change compare execution semantics.
- Change command selection behavior.
- Redesign footer or main comparison results layout.

## Decisions

Render the compare action bar after the business table grid in the drawer component tree.
Keep drawer content scrollable while pinning compare visibility using layout or sticky positioning that works with Vaadin components.
Retain and style a dedicated spacer element between command section and business table section.
Preserve all existing test IDs used by UI and browser tests.

## Risks / Trade-offs

[Sticky positioning may behave differently across containers] → Keep implementation within drawer scroll container and validate with browser tests.
[Moving controls can break existing tests] → Update unit and Playwright assertions for new order and visibility behavior.
[Reduced vertical space in tiny viewports] → Keep compact spacing and ensure compare action remains reachable and visible.

## Migration Plan

Update `MainView` drawer layout order to place compare below the table grid.
Adjust drawer styling to keep compare visible under resize conditions.
Update unit and browser tests to assert element ordering and visibility behavior.
Run relevant webapp tests to confirm no regression.

## Open Questions

Whether the compare action should be fully sticky or fixed to the drawer bottom in a future follow-up.
