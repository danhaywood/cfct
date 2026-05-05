## ADDED Requirements

### Requirement: API exposes column-value normalizer SPI
The `cfct-api` module SHALL expose a `ColumnValueNormalizer` SPI for transforming compared column values before final difference emission.
The SPI SHALL operate on per-column comparison context and left/right raw values.
Core comparison implementation SHALL support consulting multiple `ColumnValueNormalizer` implementations for one compared value pair.
Normalizers SHALL be applied in deterministic order.

#### Scenario: Multiple normalizers contribute to one value decision
- **WHEN** core comparison evaluates one differing compared column and two or more `ColumnValueNormalizer` implementations are available
- **THEN** core comparison applies each normalizer in order and uses the final normalized values for equality checks and output values

#### Scenario: No normalizer changes a value
- **WHEN** core comparison evaluates one differing compared column and all normalizers return unchanged values
- **THEN** core comparison keeps raw values and reports a difference when raw values are not equal

### Requirement: Extended-property normalizer supports cfct.normalizeMask
The system SHALL provide a default `ColumnValueNormalizer` that reads SQL Server column extended property `cfct.normalizeMask`.
When a configured mask pattern matches a value fragment, the normalizer SHALL replace the matching fragment with the mask literal.
The normalizer SHALL preserve all non-matching fragments in original order.
When `cfct.normalizeMask` is missing or does not match, the normalizer SHALL leave the value unchanged.

#### Scenario: Masked timestamp fragment is scrubbed
- **WHEN** a compared column has `cfct.normalizeMask` set to `yyyy-MM-ddThh:MM.ss.SSS` and the value starts with `2026-04-20T14:08:09.050`
- **THEN** the normalized value starts with `yyyy-MM-ddThh:MM.ss.SSS` and preserves trailing business text

#### Scenario: Unmatched mask leaves value unchanged
- **WHEN** a compared column has `cfct.normalizeMask` but the value contains no fragment matching that mask
- **THEN** the normalizer returns the original value unchanged
