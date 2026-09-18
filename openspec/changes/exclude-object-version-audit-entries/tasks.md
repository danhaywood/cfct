## 1. Audit eligibility filtering

- [ ] 1.1 Filter audit descriptors whose member identifier is exactly `objectVersion` before building each scope's semantic-key histogram.
- [ ] 1.2 Derive foreground and background app-a and app-b totals from the filtered descriptor sets.
- [ ] 1.3 Preserve deterministic comparison and difference ordering for every remaining audit member.

## 2. Comparison verification

- [ ] 2.1 Add unit coverage showing objectVersion-only foreground and background differences produce clean comparison results.
- [ ] 2.2 Add unit coverage showing mixed inputs exclude objectVersion records from totals while retaining genuine semantic-key differences.
- [ ] 2.3 Add unit coverage confirming the exclusion is exact and does not suppress differently named audit members.
- [ ] 2.4 Verify automation comparison responses expose the filtered totals and unchanged JSON structure.

## 3. Documentation and validation

- [ ] 3.1 Document that `objectVersion` is excluded from audit semantic comparison and scope totals because it is persistence-implementation noise.
- [ ] 3.2 Run the affected unit and integration test suites and resolve regressions.
