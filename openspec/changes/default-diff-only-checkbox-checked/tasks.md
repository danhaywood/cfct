## 1. Results Filter Default State

- [x] 1.1 Update results-stage initialization so `Differences only` defaults to checked when comparison results first render.
- [x] 1.2 Ensure initial visible result tabs are filtered to only tables with differences under the new default state.

## 2. Filter Behavior Safety

- [x] 2.1 Verify unchecked behavior still shows all compared tables that match text filtering.
- [x] 2.2 Verify differences-only and compared-table text filter composition remains unchanged.

## 3. Test Updates

- [x] 3.1 Update or add unit tests asserting `Differences only` is checked by default on initial results render.
- [x] 3.2 Update or add browser-level tests asserting unchanged tables are hidden by default and can be restored by unchecking the filter.

## 4. Verification

- [x] 4.1 Run relevant webapp unit tests for comparison results filter behavior.
- [x] 4.2 Run relevant browser-level tests for results-tab visibility and differences-only filtering.
