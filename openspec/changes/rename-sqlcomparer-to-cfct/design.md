## Context

The repository and user-facing assets currently use the name `sqlcomparer`.
The target name is `cfct`, meaning `Command Footprint Comparison Tool`.
The script entrypoint also needs to reflect the new name for discoverability and consistency.

## Goals / Non-Goals

**Goals:**
- Establish `cfct` as the canonical short name.
- Ensure user-facing references expand `cfct` as `Command Footprint Comparison Tool` where helpful.
- Rename `scripts/comparedb.sh` to `scripts/cfct.sh`.
- Preserve script behavior and invocation options.

**Non-Goals:**
- Deep Java package or artifact-coordinate renames in this change.
- Functional changes to comparison logic.
- Backward-compatible command aliasing unless explicitly requested.

## Decisions

Use `cfct` as the default name in user-facing docs, examples, and script references.
Treat `Command Footprint Comparison Tool` as the canonical long-form expansion in documentation.
Rename `scripts/comparedb.sh` to `scripts/cfct.sh` and update all repository references.
Keep script contents functionally equivalent apart from naming-related usage/help text updates.

## Risks / Trade-offs

[Users may still run the old script path] → Update docs clearly and consider a compatibility wrapper in a follow-up change.
[Partial rename causes mixed identity] → Include repository-wide reference updates in this task set.
[Over-scoping into internal refactors] → Limit this change to user-visible naming and launcher script rename.

## Migration Plan

Rename the script file to `scripts/cfct.sh`.
Update references in README, docs, tests, and scripts to use the new path.
Update user-facing name strings from `sqlcomparer` to `cfct` where in scope.
Run tests and script checks that cover startup or invocation paths.

## Open Questions

Whether to keep a temporary `scripts/comparedb.sh` shim that forwards to `scripts/cfct.sh`.
Whether Maven artifact or module names should be renamed in a dedicated follow-up change.
