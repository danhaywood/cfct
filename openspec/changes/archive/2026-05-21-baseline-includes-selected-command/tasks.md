## 1. Baseline filter semantics update

- [x] 1.1 Locate baseline timestamp filter evaluation in command-grid filtering logic and confirm current strict-after behavior.
- [x] 1.2 Update baseline comparison to inclusive semantics so rows at baseline timestamp remain visible.
- [x] 1.3 Verify context-menu "set baseline from selected command" keeps selected command row visible and first in current timestamp ordering.

## 2. Regression coverage

- [x] 2.1 Add or update UI/component tests asserting selected baseline command remains visible after set-baseline action.
- [x] 2.2 Add or update tests asserting baseline filtering uses greater-than-or-equal semantics.
- [x] 2.3 Run targeted webapp command-selection tests and confirm no regressions in existing filter composition behavior.
