## 1. Baseline state and UI controls

- [ ] 1.1 Add baseline timestamp state to the command-selection view model as an optional value.
- [ ] 1.2 Render a baseline timestamp input field above the command selection grid and bind it to baseline state.
- [ ] 1.3 Implement baseline input validation and clear behavior so invalid edits are rejected and empty value disables baseline filtering.

## 2. Command-grid filtering behavior

- [ ] 2.1 Extend the command-grid filtering pipeline to include baseline timestamp filtering with strict `>` semantics.
- [ ] 2.2 Ensure baseline filtering composes correctly with member, interactionId, and replay-state filters.
- [ ] 2.3 Preserve existing default grid behavior when baseline is unset.

## 3. Context menu baseline action

- [ ] 3.1 Add a command-row context menu action labeled to set baseline from selected command timestamp.
- [ ] 3.2 Wire the context-menu action to update baseline state and refresh grid rows immediately.
- [ ] 3.3 Ensure baseline-driven filter changes clear stale comparison status reports consistently with other command-grid parameter changes.

## 4. Verification

- [ ] 4.1 Add or update UI/component tests covering baseline field rendering, manual baseline edits, and clear behavior.
- [ ] 4.2 Add or update tests covering context-menu baseline assignment and strict after-baseline row filtering.
- [ ] 4.3 Run the project test suite for impacted modules and confirm no regressions in existing command-grid filtering behavior.
