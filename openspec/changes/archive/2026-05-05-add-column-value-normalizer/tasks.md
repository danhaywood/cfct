## 1. API and metadata model updates

- [x] 1.1 Add `ColumnValueNormalizer` SPI contract to `cfct-api` with inputs needed for per-column left/right value normalization.
- [x] 1.2 Extend core comparison metadata/context models so normalizers can access column identity and configured extended properties.
- [x] 1.3 Wire `List<ColumnValueNormalizer>` into core comparison services with deterministic ordering.

## 2. Mask-based normalizer implementation

- [x] 2.1 Implement default SQL Server normalizer that reads column extended property `cfct.normalizeMask`.
- [x] 2.2 Implement mask-matching and scrubbing logic that replaces matching fragments with the mask literal while preserving surrounding text.
- [x] 2.3 Add guard behavior for missing, unsupported, or non-matching masks so values remain unchanged.

## 3. Comparison pipeline integration

- [x] 3.1 Keep SQL candidate-difference query behavior unchanged so raw value-different rows are still returned.
- [x] 3.2 Apply normalizers to candidate matched-row compared values before final per-column difference emission.
- [x] 3.3 Suppress row differences when all compared columns become equal after normalization.

## 4. Verification and documentation

- [x] 4.1 Add unit tests for SPI composition ordering and no-op behavior when no normalizer changes values.
- [x] 4.2 Add tests for `cfct.normalizeMask` behavior, including the timestamp example and unmatched-mask cases.
- [x] 4.3 Add integration or characterization tests proving SQL reports candidate diffs but client normalization suppresses timestamp-only noise.
- [x] 4.4 Update developer documentation with `cfct.normalizeMask` setup guidance and supported mask semantics.
