## Why

Causeway's JDO and JPA persistence paths do not consistently emit or bump the `objectVersion` member, so its audit entries create structural differences that do not represent meaningful application regressions.
CFCT should remove this persistence-implementation noise from audit comparison and from the per-scope audit totals.

## What Changes

- Exclude audit entries whose member identifier is exactly `objectVersion` before comparing semantic-key occurrence counts.
- Exclude those entries before calculating foreground and background `appACount` and `appBCount` totals.
- Apply the exclusion consistently to both database sides and both audit scopes.
- Add tests covering clean results, genuine remaining differences, and totals after exclusion.
- Document the excluded technical member in CFCT's audit comparison behavior.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `command-audit-trail-comparison`: Define `objectVersion` audit entries as out of scope for semantic-key comparison and record totals.

## Impact

The change affects the audit-trail comparison service, its tests, automation JSON totals, and audit comparison documentation.
The JSON shape remains unchanged, but reported counts and differences no longer include `objectVersion` entries.
Business-table comparison behavior and top-level business `hasDifferences` semantics remain unchanged.
