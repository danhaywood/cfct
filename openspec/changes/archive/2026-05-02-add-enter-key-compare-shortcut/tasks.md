## 1. Enter Default-Action Wiring

- [x] 1.1 Add Enter-key handling in the main selection workflow that delegates to the existing compare execution path.
- [x] 1.2 Gate Enter activation with the same enabled-state rules as the Compare button.
- [x] 1.3 Ensure Enter handling ignores login and text-input editing contexts.

## 2. UI Semantics and Accessibility

- [x] 2.1 Mark Compare semantics as the default action for the selection workflow in UI wiring.
- [x] 2.2 Preserve existing click behavior and in-flight compare disablement to avoid duplicate execution.

## 3. Automated Tests

- [x] 3.1 Add or update unit tests proving Enter triggers compare when eligible selections exist.
- [x] 3.2 Add or update unit tests proving Enter does not trigger compare when compare is disabled.
- [x] 3.3 Add or update Playwright tests covering Enter activation from command-driven selection flow.

## 4. Verification and Docs

- [x] 4.1 Run relevant webapp unit and browser-level tests for Enter-triggered compare behavior.
- [x] 4.2 Update README workflow notes to document Enter as compare accelerator/default action.
