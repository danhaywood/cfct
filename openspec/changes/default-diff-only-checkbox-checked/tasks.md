## 1. Results Filter Default State

- [ ] 1.1 Update results-stage initialization so `Differences only` defaults to checked when comparison results first render.
- [ ] 1.2 Ensure initial visible result tabs are filtered to only tables with differences under the new default state.

## 2. Filter Behavior Safety

- [ ] 2.1 Verify unchecked behavior still shows all compared tables that match text filtering.
- [ ] 2.2 Verify differences-only and compared-table text filter composition remains unchanged.

## 3. Test Updates

- [ ] 3.1 Update or add unit tests asserting `Differences only` is checked by default on initial results render.
- [ ] 3.2 Update or add browser-level tests asserting unchanged tables are hidden by default and can be restored by unchecking the filter.

## 4. Verification

- [ ] 4.1 Run relevant webapp unit tests for comparison results filter behavior.
- [ ] 4.2 Run relevant browser-level tests for results-tab visibility and differences-only filtering.
