## Context

The Vaadin webapp already supports manual table selection, connectivity status, and comparison execution.
Recent UI changes introduced a navigation area and screenshots, but interaction priorities are still unclear in the main screen.
The Compare action is not visually anchored near primary navigation context, footer labels add noise, and table column behavior is not optimized for readability.
Playwright screenshot expectations currently cover existing expanded state presentation but need refreshed coverage for the collapsed-navigation visual state.
The design must keep existing comparison behavior intact while improving layout, clarity, and screenshot coverage.

## Goals / Non-Goals

**Goals:**
- Place the Compare action in the navigation/menu region above the table and right-aligned.
- Simplify footer/status rendering by removing redundant labels, adding spacing, and right-aligning the status indicator.
- Improve table readability by auto-sizing schema column width and center-aligning selection controls without `Select` header text.
- Capture and verify a stable collapsed-navigation visual state in screenshots.
- Refresh Playwright screenshots for updated expanded and collapsed navigation states.

**Non-Goals:**
- No change to comparison backend logic, datasource behavior, or selection-plan semantics.
- No redesign of business-key eligibility rules beyond layout/presentation updates.
- No introduction of new persistent navigation labels or badges in the navbar.

## Decisions

- Use an action container in the top navigation/layout region to host the Compare button above the grid.
  - Rationale: keeps the primary action near contextual controls and reduces scan time.
  - Alternative considered: keep Compare near grid footer.
  - Rejected because it competes with status information and weakens action discoverability.
- Refactor footer/status bar into a lightweight horizontal layout with spacing utilities and right-aligned status text.
  - Rationale: cleaner visual hierarchy and less redundant labeling.
  - Alternative considered: retain existing labels and only tweak styles.
  - Rejected because it preserves clutter and does not meet requested alignment intent.
- Update grid column configuration to auto-size the schema column and center-align select controls with blank header text.
  - Rationale: schema values vary in length and selection controls are visually cleaner when centered under an unlabeled utility column.
  - Alternative considered: fixed schema width and existing `Select` header.
  - Rejected because it wastes space and adds redundant header noise.
- Keep the navbar minimal by removing the experimental collapsed-state label and relying on drawer behavior plus screenshot coverage.
  - Rationale: the label added noise and did not improve usability in practice.
  - Alternative considered: keep persistent collapsed-state text.
  - Rejected because it distracted from primary actions.
- Update Playwright screenshot generation/approval files to include collapsed-navigation capture and revised expanded layout.
  - Rationale: protects the intended UX contract and avoids regressions in future UI updates.

## Risks / Trade-offs

- [Risk] Layout changes may cause fragile screenshot diffs across environments.
  → Mitigation: stabilize viewport, deterministic data fixtures, and explicit screenshot regions where appropriate.
- [Risk] Auto-sizing schema column may affect performance on large datasets.
  → Mitigation: apply auto-sizing during initialization and avoid repeated recalculation on every refresh.
- [Risk] Removing header text for select column may reduce accessibility context.
  → Mitigation: ensure aria-label or tooltip semantics are present for selection controls.
- [Risk] Collapsed affordance choice may conflict with existing theme tokens.
  → Mitigation: implement using current theme variables and validate in both light/dark modes if applicable.

## Implementation note

The experimental collapsed-state navbar hint was removed after review because it added clutter without improving interaction clarity.
Collapsed-state behavior is still covered by deterministic Playwright screenshots.
