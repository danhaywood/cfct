## Context

The webapp now has a fixed footer status bar and a sticky compare action in the drawer.
In smaller viewports, these can visually collide if spacing is not reserved in the drawer flow.
The footer also currently includes JDBC server URL text, which users asked to suppress in favor of concise database context only.
The account menu currently uses a generic label and does not expose the authenticated username in the top-right navbar.

## Goals / Non-Goals

**Goals:**
- Ensure the bottom status bar does not overlap the compare action region.
- Show only left and right database names in footer connection context, not JDBC URL.
- Show authenticated username in the account menu label.
- Improve horizontal padding and spacing between footer labels for readability.

**Non-Goals:**
- No changes to authentication protocol, credential storage, or connection validation behavior.
- No changes to comparison execution semantics or result rendering logic.
- No backend API or persistence changes.

## Decisions

1.
Reserve additional bottom breathing room in the main content and drawer interaction area to account for fixed footer height.
This keeps the compare action visible and non-overlapping in constrained heights.
Alternative considered: changing footer from fixed to static flow.
Rejected because fixed status context is intentionally persistent.

2.
Reduce footer connection detail surface to database-pair text only.
The JDBC URL is intentionally omitted from rendered UI to reduce noise and accidental infrastructure exposure.
Alternative considered: truncating URL.
Rejected because any URL display still adds clutter and limited user value.

3.
Use authenticated username as part of the account menu top-level label (e.g., `Account: <username>` or `<username>` depending available value).
Fallback to existing generic label when username cannot be resolved.
Alternative considered: username only in submenu.
Rejected because primary visibility was requested in top-right menu label itself.

4.
Standardize footer horizontal spacing using explicit gaps and horizontal padding tokens.
This ensures readable separation between state, summary, and progress labels.
Alternative considered: per-label manual margins.
Rejected because it is less maintainable and harder to keep consistent.

## Risks / Trade-offs

[Risk] Username may be unavailable for some authenticated contexts.
→ Mitigation: keep deterministic fallback label and avoid null/blank rendering artifacts.

[Risk] Footer spacing increases horizontal footprint on narrow widths.
→ Mitigation: preserve wrap behavior and prioritize readability over tight packing.

[Risk] Extra bottom spacing may slightly reduce visible content area.
→ Mitigation: use minimal practical offset tied to footer height and verify responsive behavior in existing UI tests.

## Migration Plan

This is a UI-only update with no data migration.
Deploy in one release with regression coverage in unit and browser tests.
Rollback is a straightforward code revert.

## Open Questions

Whether account label should be `Account: <username>` or only `<username>` for final UX copy.
Whether footer database names should include explicit left/right prefixes or remain compact with a separator.