## 1. Refresh orchestration update

- [ ] 1.1 Locate the refresh action flow in the command-selection UI logic and keep the existing reload, clear, and latest-`OK` auto-selection sequence intact.
- [ ] 1.2 Add a post-refresh eligibility check that uses the same compare-enabled criteria already used by the Compare action.
- [ ] 1.3 Invoke compare automatically from Refresh only when eligible, using the same compare orchestration entrypoint as the Compare button/Enter action.
- [ ] 1.4 Ensure refresh remains a no-op for compare execution when no eligible `OK` command or table selection exists.

## 2. Test coverage and regression safety

- [ ] 2.1 Update or add automated tests to verify Refresh still auto-selects the most recent `OK` command.
- [ ] 2.2 Add a positive-path test that verifies Refresh triggers compare automatically when compare becomes enabled.
- [ ] 2.3 Add a negative-path test that verifies Refresh does not trigger compare when eligibility is not satisfied.
- [ ] 2.4 Run the relevant test suite and confirm no regressions in existing refresh shortcut and selection behaviors.
