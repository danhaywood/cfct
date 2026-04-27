## Context

The Vaadin webapp already has a home page that displays SQL connectivity status and a manual table-selection stage.
The existing selection UI is intentionally simple and is ready to evolve into a richer left-hand selection surface.
The configured connection values already exist as typed webapp configuration properties, but the main UI does not yet place those details in a persistent footer.
Playwright coverage already validates connectivity status and manual selection behavior, so this change should extend that path rather than create a separate browser-test mechanism.

## Goals / Non-Goals

**Goals:**
- Provide a main layout structure with a hamburger menu, content area, and footer.
- Render configured connection details in the footer using deterministic labels and test selectors.
- Replace the left-hand table-selection list with a Vaadin Grid that supports client-visible sorting and filtering.
- Preserve eligibility, selection count, and disabled ineligible-table behavior.
- Add a placeholder `Compare` button whose enabled state follows whether one or more eligible tables are selected.
- Cover deterministic state transitions with unit tests where practical and Playwright for the happy browser path.

**Non-Goals:**
- Do not implement comparison execution from the `Compare` button.
- Do not change the comparison API, datasource wiring, CLI behavior, or table discovery rules.
- Do not introduce new navigation destinations behind the hamburger menu unless required to render the menu safely.
- Do not replace existing SQL connectivity validation behavior.

## Decisions

- Use a Vaadin `AppLayout`-style shell or equivalent reusable layout component for the hamburger menu, main content, and footer.
  Alternative considered: keep all markup inside the home view.
  A reusable shell is preferable because navigation and footer concerns are layout-level concerns and future pages can reuse them.

- Use Vaadin Grid for the left-hand table-selection surface.
  Alternative considered: enhance the existing checkbox list with custom filtering controls.
  Grid is preferable because sorting, filtering, keyboard behavior, stable row rendering, and later column additions are first-class Vaadin concerns.

- Keep selection state in the existing view model or a small dedicated presentation model rather than in Grid component internals only.
  Alternative considered: derive all state directly from selected Grid items.
  A presentation model is preferable because selected-count feedback, button enablement, unit tests, and future comparison execution all need a deterministic selected-table set.

- Implement filtering with explicit filter fields above or inside Grid header rows.
  Alternative considered: rely only on browser find or built-in visual sorting.
  Explicit filter controls are preferable because Playwright can exercise them deterministically and users can narrow the table catalog without changing selection state unexpectedly.

- Render the `Compare` button as enabled only when the selected-table set is non-empty and attach no execution side effect beyond optional placeholder feedback.
  Alternative considered: wire the button into partial comparison orchestration now.
  A no-op placeholder is preferable because execution behavior is out of scope and the UI contract can be tested independently.

- Extend existing Playwright tests and selectors rather than adding a second browser-test stack.
  Alternative considered: create a new standalone Playwright project for this UI update.
  Reusing the current path is preferable because Testcontainers setup, headless execution, and CI-style assumptions are already established.

## Risks / Trade-offs

- Grid virtualization can make browser assertions brittle if tests depend on invisible rows.
  Mitigation: assert against visible happy-path rows and use filters that narrow the result set before checking row content.

- Footer connection details could expose sensitive values if credentials are included.
  Mitigation: display server and database identities, but never display the configured password and mask or omit other sensitive fields.

- Vaadin component tests can be expensive if they require full UI bootstrapping.
  Mitigation: put enablement and filtering logic behind small presentation methods where possible and reserve browser tests for integrated behavior.

- The hamburger menu might appear empty in the initial release.
  Mitigation: provide a minimal menu affordance with deterministic accessible labeling and leave future destinations out of scope.

## Migration Plan

No data migration is required.
Existing users receive the updated webapp layout when the webapp module is rebuilt and redeployed.
Rollback is limited to reverting the webapp UI and test changes because no persistence, API, or schema contracts change.

## Open Questions

- Should the footer show username, or should it only show server and database names to minimize credential-related exposure?
- Which table columns are guaranteed in the current table catalog and should therefore be included in the first Grid release?
