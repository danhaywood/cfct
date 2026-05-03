## Context

README currently points contributors to `./scripts/test-webapp-playwright-connectivity.sh` for webapp Playwright connectivity checks.
Current project behavior indicates that this path is unreliable or broken in at least some expected local flows.
The existing `webapp-playwright-connectivity-status` capability requires a scriptable headless execution path that works in CI-style environments.
The project therefore needs a single verified command contract, with docs and implementation aligned.

## Goals / Non-Goals

**Goals:**
- Decide whether the helper script is worth preserving based on reliability and maintenance value.
- Ensure at least one deterministic headless execution path remains available for local and CI usage.
- Update README so it documents only verified commands and expected prerequisites.
- Reduce future drift between documented test commands and runnable project scripts.

**Non-Goals:**
- Redesign Playwright test coverage or test scenarios beyond what is needed for command-path reliability.
- Change unrelated webapp UI behavior, fixtures, or comparison logic.
- Introduce a new test framework or CI platform migration.

## Decisions

- Decision: Use a value-based fork for the helper script path.
  - Option A: Keep script if it can be made deterministic with clear prerequisites and minimal wrapper complexity.
  - Option B: Remove script if it duplicates a stable direct command and adds maintenance drift risk.
  - Rationale: The user-facing contract is reliable execution, not script existence.

- Decision: Treat README command examples as executable contract tests.
  - Rationale: Broken README commands create onboarding and CI failures; docs must match live workflow.
  - Alternative considered: Keep descriptive docs without strict validation.
  - Rejected because this allows repeated drift.

- Decision: Keep capability semantics focused on scriptable headless execution, independent of script-vs-command packaging.
  - Rationale: Spec should require dependable execution path while permitting implementation flexibility.

## Risks / Trade-offs

- [Risk] Removing the script may disrupt users who already rely on that entrypoint.
  → Mitigation: Provide a direct replacement command in README and note migration path in change artifacts.

- [Risk] Keeping the script may preserve hidden fragility if wrapper logic diverges from actual test wiring.
  → Mitigation: Minimize wrapper behavior and ensure it delegates to one canonical underlying command.

- [Risk] Environment prerequisites (Docker/Testcontainers) may still cause perceived flakiness.
  → Mitigation: Document prerequisites explicitly beside the command and avoid implying lightweight execution.
