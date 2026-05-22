## 1. Keyboard accelerator behavior

- [x] 1.1 Add `F5` shortcut handling to trigger existing command refresh logic.
- [x] 1.2 Add `Alt+R` shortcut handling to trigger existing command refresh logic.
- [x] 1.3 Ensure shortcut handling does not trigger while typing in inputs and prevents browser reload when consumed.

## 2. Regression coverage

- [x] 2.1 Update `MainViewTest` to verify `F5` and `Alt+R` invoke refresh behavior.
- [x] 2.2 Add tests that accelerators are ignored for text/date/time input focus.
- [x] 2.3 Run targeted `cfct-webapp` command-selection tests.
