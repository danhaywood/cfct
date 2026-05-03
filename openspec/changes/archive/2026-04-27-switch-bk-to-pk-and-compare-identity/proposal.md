## Why

The current comparison rule assumes `_BK` unique indexes and ignores identity-style `id` values, but the target database conventions now rely on `_PK` primary-key-style constraints or unique indexes.
Aligning metadata discovery and comparison behavior with `_PK` avoids false setup failures and ensures identity values are compared when they are part of the selected key model.

## What Changes

- Switch core key-discovery convention from `_BK` to `_PK` by default, while still requiring a single deterministic key source per table.
- Treat identity columns as comparable values unless explicitly ignored by caller options.
- **BREAKING**: default comparison behavior no longer ignores `id` values automatically.
- Update fixture schemas and data to use `_PK` naming and to exercise identity-value comparison behavior under the new default.
- Update webapp manual-selection eligibility so selectable tables are determined by `_PK` rule instead of `_BK` rule.
- Update automated tests, including Playwright selection assertions, to reflect `_PK` eligibility and identity-column comparison semantics.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `core-single-table-comparison`: Business-key discovery and ignored-column defaults change from `_BK` + implicit `id` ignore to `_PK` + explicit ignore-only behavior.
- `core-multi-table-comparison`: Multi-table behavior inherits the new `_PK` discovery rule and no-default-identity-ignore behavior.
- `purchase-order-comparison-fixture`: Fixture contracts and scenarios change to `_PK` naming and identity-aware comparison outcomes.
- `vaadin-webapp-configuration`: Manual table-selection eligibility changes from `_BK` convention to `_PK` convention.

## Impact

This impacts metadata discovery in `cfct-impl`, default comparison options in API contracts, fixture SQL resources, and webapp table-catalog eligibility logic.
It will update integration and browser tests that currently assume `_BK` eligibility and ignored identity differences.
Documentation and examples will need to reflect the new `_PK` convention and changed default comparison behavior.
