## Why

The project name should move from `sqlcomparer` to `cfct`.
`cfct` stands for `Command Footprint Comparison Tool`.
A consistent rename improves branding and reduces mixed naming in docs and scripts.
The shell entrypoint should align with the new name.

## What Changes

Rename project-facing naming from `sqlcomparer` to `cfct` across user-visible surfaces.
Rename `comparedb.sh` to `cfct.sh`.
Update references and usage examples that point to the old script name.
Maintain existing behavior and arguments after the rename.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `project-naming-and-entrypoints`: Standardize user-visible naming to `cfct` and update launcher script naming.

## Impact

This change affects docs, scripts, and any user-facing labels that still reference `sqlcomparer`.
This change may require follow-up internal renames for package/module names in a later phase.
