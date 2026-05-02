## Context

The webapp currently emphasizes functionality but has minimal product identity in both unauthenticated and authenticated states.
Users first see a login modal and then a main AppLayout shell, and both are suitable anchor points for consistent branding.
Branding must be lightweight and not disrupt existing login validation, table selection, or comparison workflows.

## Goals / Non-Goals

**Goals:**
- Introduce a reusable logo/icon asset in webapp resources.
- Display branding on the login experience with the logo positioned on the right-hand side of the login content.
- Display a compact logo and `CFCT` product name in the authenticated navbar.
- Keep branding testable via deterministic identifiers.

**Non-Goals:**
- Redesigning full page theming or color system is out of scope.
- Replacing login flow behavior or authentication validation is out of scope.
- Supporting runtime brand customization is out of scope.

## Decisions

Store the logo/icon under `cfct-webapp/src/main/resources/images` so it can be served as a static classpath resource.
Use Vaadin image components in both login and main view for consistent rendering.
Add a right-side branding block to the login form container with responsive layout behavior for smaller widths.
Add a compact navbar branding block near the drawer toggle that includes a small icon and a `CFCT` text label.
Add stable `data-testid` attributes for login branding and navbar branding elements for automated tests.

## Risks / Trade-offs

[Login layout crowding] → Use responsive spacing and allow vertical stacking on narrow viewports.
[Asset scaling artifacts] → Provide an appropriately sized source image and constrain rendered dimensions in CSS.
[Test brittleness from visual changes] → Assert deterministic IDs and text presence instead of pixel-level assumptions.
