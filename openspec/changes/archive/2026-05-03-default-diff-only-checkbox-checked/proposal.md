## Why

Users typically focus on changed tables first, so showing unchanged tables by default adds noise and slows triage.
Defaulting the differences-only filter to checked makes the first results view align with the most common comparison workflow.

## What Changes

- Change the comparison results `Differences only` checkbox default state to checked when results first render.
- Keep the existing filter behavior and composition with compared-table text filtering unchanged.
- Update tests to assert the new default and visible-tab behavior on initial results render.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `webapp-differences-only-results-filter`: Change initial filter state so unchanged tables are hidden by default when results first render.

## Impact

This affects webapp results-stage state initialization and tab visibility behavior.
This affects unit and browser-level tests that currently expect the filter to start unchecked.
No backend API, comparison engine, or output format changes are required.
