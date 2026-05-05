## Why

Some compared values differ only in timestamp fragments that are expected to vary between environments and runs.
Ignoring entire columns hides meaningful non-timestamp differences and reduces comparison fidelity.
We need a way to normalize volatile substrings so semantically equivalent values can still match.

## What Changes

- Add a new `ColumnValueNormalizer` SPI that can transform raw compared values before final row-difference materialization.
- Add a default implementation that reads SQL Server column extended property `cfct.normalizeMask` and applies mask-based scrubbing when a matching fragment is found.
- Keep SQL-side comparison broad so potentially different rows are still returned, then run client-side normalization in Java before deciding whether to keep or suppress a row difference.
- Ensure rows that differ only by normalized fragments are suppressed from final UI-visible differences.
- Add automated tests that prove mask-driven normalization preserves non-masked content and suppresses timestamp-only noise.

## Capabilities

### New Capabilities
- `column-value-normalizer-spi`: Define pluggable value normalization contracts and default mask-based implementation driven by `cfct.normalizeMask`.

### Modified Capabilities
- `core-single-table-comparison`: Row-difference finalization now applies client-side value normalization and suppresses rows whose compared values become equal after normalization.

## Impact

This change affects core comparison orchestration, compared-value difference evaluation, and SQL Server metadata lookup usage.
This change adds a new API SPI and a default implementation in core modules.
This change affects UI-observable comparison output because some previously reported differences will now be suppressed after normalization.
No database schema changes are required.
