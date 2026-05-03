## 1. Assess current Playwright connectivity command path

- [ ] 1.1 Reproduce the current README webapp Playwright connectivity command path and capture the exact failure mode.
- [ ] 1.2 Inspect `scripts/test-webapp-playwright-connectivity.sh` to determine whether the script can be repaired with low maintenance overhead.
- [ ] 1.3 Decide and document implementation direction: repair script or retire script in favor of a direct canonical command.

## 2. Implement the selected execution-path fix

- [ ] 2.1 If keeping the script, update script internals and prerequisites so it reliably launches headless connectivity Playwright tests.
- [ ] 2.2 If retiring the script, remove or deprecate it and ensure a direct replacement command is available and stable.
- [ ] 2.3 Validate that the supported command works in a non-interactive CI-style run with required Docker/Testcontainers prerequisites.

## 3. Align README and verification guardrails

- [ ] 3.1 Update README webapp testing section to reference only the supported executable command path.
- [ ] 3.2 Remove or replace stale references to broken Playwright connectivity script usage.
- [ ] 3.3 Run the documented command from README and confirm it begins or completes execution without path-resolution errors.
