## Why

Primary key/business key discovery currently relies on overly strict naming expectations for index or constraint names.
This causes legitimate keys such as `PurchaseOrder__reference__PK` to be missed even though they clearly end with the `_PK` suffix.

## What Changes

- Update business key discovery to match index and constraint names by suffix rather than by full fixed-name pattern.
- Accept keys where the relevant token ends in `_PK`, including compound names such as `PurchaseOrder__reference__PK`.
- Preserve existing behavior for current `_PK`-suffixed names while broadening compatibility.
- Add or update automated tests to cover suffix-based matching and regression scenarios.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `core-single-table-comparison`: Relax key discovery requirements so primary/business keys can be identified from any index/constraint name that ends with the expected PK suffix.

## Impact

Comparison key-resolution logic in the core comparison module will be updated.
Unit and integration test fixtures that assert key discovery behavior will be extended.
No external API, dependency, or runtime deployment changes are expected.
