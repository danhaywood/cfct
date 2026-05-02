## 1. API Contracts and Core Progress Events

- [x] 1.1 Add a public comparison progress listener interface and immutable progress event model in API service contracts.
- [x] 1.2 Extend multi-table comparison request options to accept an optional progress listener without breaking existing callers.
- [x] 1.3 Emit deterministic per-table start and completion-or-failure progress events from core multi-table orchestration.
- [x] 1.4 Add or update core tests to verify event order, payload fields, and no-listener backward compatibility.

## 2. CLI Progress Presentation

- [x] 2.1 Register a progress listener in CLI comparison execution wiring.
- [x] 2.2 Print concise per-table progress lines during execution while preserving deterministic main output behavior.
- [x] 2.3 Ensure progress output uses stderr so stdout and file output remain dedicated to comparison artifacts.
- [x] 2.4 Add or update CLI tests for progress line behavior and execution failure handling with progress enabled.

## 3. Webapp Progress Feedback

- [x] 3.1 Register a progress listener for webapp-triggered comparison runs in the Vaadin execution flow.
- [x] 3.2 Update the footer/status bar component to render live progress text during active runs.
- [x] 3.3 Update the footer/status bar to render terminal success-or-failure status when runs complete.
- [x] 3.4 Add or update webapp tests for status bar progress updates and password-safe footer rendering.

## 4. Verification and Documentation

- [x] 4.1 Run relevant unit and integration test suites for core, CLI, and webapp modules.
- [ ] 4.2 Validate manual flows for CLI and webapp progress behavior against multi-table comparison scenarios.
- [ ] 4.3 Update release-facing documentation or notes describing optional listener adoption and new progress feedback behavior.
