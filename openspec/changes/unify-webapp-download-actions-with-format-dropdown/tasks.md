## 1. Webapp Download Outcome Extensions

- [ ] 1.1 Extend webapp comparison execution outcome to include YAML payload for the latest comparison run.
- [ ] 1.2 Ensure YAML payload generation reuses existing deterministic formatter behavior.

## 2. Unified Download Control UI

- [ ] 2.1 Replace separate JSON/Excel download buttons with one format selector and one download action.
- [ ] 2.2 Add `json`, `yaml`, and `excel` selector options and default selection to `json`.
- [ ] 2.3 Rebind unified download resource and filename extension based on selected format.

## 3. Test Coverage Updates

- [ ] 3.1 Update main view tests to assert unified download control presence and default JSON selection.
- [ ] 3.2 Add or update tests for YAML selection and download resource switching behavior.

## 4. Verification and Documentation

- [ ] 4.1 Run relevant webapp unit and browser-level tests for results-stage controls.
- [ ] 4.2 Update README or release notes to mention YAML webapp download and unified download controls.
