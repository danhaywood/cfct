## 1. Baseline state and UI controls

- [x] 1.1 Add baseline timestamp state to the command-selection view model as an optional value.
- [x] 1.2 Render a baseline timestamp input field above the command selection grid and bind it to baseline state.
- [x] 1.3 Implement baseline input validation and clear behavior so invalid edits are rejected and empty value disables baseline filtering.

## 2. Command-grid filtering behavior

- [x] 2.1 Extend the command-grid filtering pipeline to include baseline timestamp filtering with strict `>` semantics.
- [x] 2.2 Ensure baseline filtering composes correctly with member, interactionId, and replay-state filters.
- [x] 2.3 Preserve existing default grid behavior when baseline is unset.

## 3. Context menu baseline action

- [x] 3.1 Add a command-row context menu action labeled to set baseline from selected command timestamp.
- [x] 3.2 Wire the context-menu action to update baseline state and refresh grid rows immediately.
- [x] 3.3 Ensure baseline-driven filter changes clear stale comparison status reports consistently with other command-grid parameter changes.

## 4. Verification

- [x] 4.1 Add or update UI/component tests covering baseline field rendering, manual baseline edits, and clear behavior.
- [x] 4.2 Add or update tests covering context-menu baseline assignment and strict after-baseline row filtering.
- [x] 4.3 Run the project test suite for impacted modules and confirm no regressions in existing command-grid filtering behavior.
