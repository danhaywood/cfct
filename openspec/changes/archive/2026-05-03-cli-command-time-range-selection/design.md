## Context

The CLI currently requires table selection input (`-t` or table file) and does not support selecting commands by timestamp window.
The codebase already includes command-audit-footprint resolution capabilities that map command interactions to touched business tables.
The requested workflow is to provide a command time range, select matching commands inclusively, infer touched tables, and compare those tables.

## Goals / Non-Goals

**Goals:**
- Add CLI arguments for command time-range selection.
- Make start and end boundaries inclusive.
- Build the selected command set from that time range and infer comparison tables from it.
- Enforce clear validation for mutually-exclusive table-selection modes.
- Update tests and README for discoverability and regression safety.

**Non-Goals:**
- Changing how command-footprint resolution maps logical types to tables.
- Changing comparison output formats or renderer behavior.
- Adding persisted presets or saved query profiles.

## Decisions

- Introduce explicit CLI options for range start and range end timestamps.
The parser will require both values when this mode is used.
- Treat range matching as inclusive at both ends.
This aligns with user expectation for operational windows.
- Keep table-selection modes exclusive.
Users must choose either explicit table input (`-t`/file) or time-range command selection.
- Reuse existing command selection and table inference services.
This minimizes new domain logic and keeps behavior consistent across CLI and webapp paths.
- Fail fast when no commands or no inferred business tables are found for the requested range.
This prevents confusing empty comparisons.

## Risks / Trade-offs

- [Timestamp parsing ambiguity] → Document accepted format(s) and add parser tests for invalid/edge values.
- [Large time windows may infer many tables] → Keep behavior explicit in CLI output and leave optimization for later.
- [Mutual-exclusion complexity] → Centralize validation rules with focused tests.
