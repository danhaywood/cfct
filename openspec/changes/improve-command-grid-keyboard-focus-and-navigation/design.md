## Context

The command selection grid already supports checkbox-based multi-select and command-driven table auto-selection.
After login, users currently remain in a generic UI state and must manually move focus before keyboard interaction.
The requested behavior is a keyboard-first flow where users can immediately navigate command rows and toggle selection without pointer input.

## Goals / Non-Goals

**Goals:**
- Move focus to command grid after successful login.
- Support Space to toggle the focused command row selection.
- Ensure arrow keys provide useful row/cell navigation consistent with Vaadin grid semantics.
- Keep behavior deterministic and testable.

**Non-Goals:**
- Full custom keyboard remapping beyond Space and arrow keys is out of scope.
- Replacing Vaadin grid internals with custom table widgets is out of scope.
- Changing comparison logic or command-to-table resolution is out of scope.

## Decisions

Use Vaadin-supported focus APIs to focus command grid during post-login UI transition.
Attach keyboard event handling for Space on focused command-row context to trigger the same selection update path as checkbox toggles.
Prefer native Vaadin arrow-key navigation behavior and only add targeted guards to avoid regressions.
Add deterministic test hooks and tests around post-login focus target and keyboard-driven selection changes.

## Risks / Trade-offs

[Keyboard handler conflicts with native grid shortcuts] → Scope custom handling narrowly to Space toggle and preserve default arrow navigation.
[Focus timing race after login modal close] → Set focus in UI thread after catalog reload and modal state update.
[Test fragility for keyboard behavior] → Assert stable state transitions and selectors instead of relying on visual assumptions.
