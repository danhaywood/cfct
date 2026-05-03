## Context

The results stage currently initializes the `Differences only` checkbox as unchecked.
This causes unchanged tables to appear by default even though users usually want to inspect differences first.
The behavior is owned by webapp results UI state and already composes with the compared-table text filter.

## Goals / Non-Goals

**Goals:**
- Initialize the differences-only filter state to enabled when new comparison results render.
- Preserve existing filter semantics and composition with compared-table text filtering.
- Update automated tests to validate the new default behavior.

**Non-Goals:**
- Changing comparison computation or table-difference detection logic.
- Changing filter labels, control placement, or other results-stage controls.
- Introducing persisted user preferences for this filter.

## Decisions

- Set the initial UI state for differences-only filtering to true at results initialization.
This is the minimal change and keeps behavior local to presentation-state setup.
- Keep the current filter predicate unchanged.
Only the default state changes, avoiding risk to filter correctness.
- Update unit and browser tests that currently assert unchecked-by-default behavior.
This keeps the specification and regression suite aligned.

## Risks / Trade-offs

- [Users expecting all tables first] → Users can uncheck immediately, and the checkbox remains explicit and discoverable.
- [Missed test assumptions about default visibility] → Update focused tests covering initial tab visibility and checkbox state.
- [Future preference requirement] → Keep initialization isolated so preference-based defaults can be introduced later.
