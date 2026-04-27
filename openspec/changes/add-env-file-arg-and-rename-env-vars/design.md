## Context

`comparedb.sh` currently relies on environment-variable overrides for selecting the env file and related wrapper behavior.
The wrapper and docs now use `comparedb` naming, but some script-level variables still use a `SQLCOMPARE_*` prefix, which is inconsistent and harder to discover.
The requested change is script-and-doc scoped and should not require Java CLI changes.

## Goals / Non-Goals

**Goals:**
- Add an explicit `--env-file <path>` wrapper argument so users can choose dotenv input directly from the command line.
- Standardize wrapper environment variable names to `COMPAREDB_*` and update docs/help output to match.
- Preserve backward compatibility for existing invocation patterns while providing a clear precedence rule.

**Non-Goals:**
- No changes to SQL comparer Java CLI argument names or parsing behavior.
- No changes to comparison logic, output generation, fixture lifecycle scripts, or Maven module structure.

## Decisions

Use wrapper-level argument parsing for `--env-file <path>` before forwarding remaining args to the Java CLI.
This allows the wrapper to control dotenv source selection while keeping pass-through behavior for all other CLI flags.
Alternative considered was requiring users to keep using env vars only, but that keeps a less ergonomic interface.

Adopt a precedence order of explicit `--env-file` argument over `COMPAREDB_ENV_FILE` environment variable over wrapper default.
This keeps command-line intent highest priority and remains predictable for automation.
Alternative considered was env var precedence over argument, but that can produce surprising behavior in shell sessions.

Rename all script-level `SQLCOMPARE_*` environment variables to `COMPAREDB_*` and update wrapper help and README examples in the same change.
This ensures no mixed prefix guidance remains in active documentation.
Alternative considered was dual-prefix long-term support, but that prolongs inconsistency and documentation complexity.

## Risks / Trade-offs

Users with existing automation using `SQLCOMPARE_*` variables may break if aliases are not provided or migration guidance is unclear.
Mitigation is to document the rename clearly in README and wrapper help, and optionally keep temporary compatibility aliases if needed during implementation.

Wrapper argument parsing can accidentally consume arguments intended for Java CLI if parsing is too broad.
Mitigation is to only intercept the exact `--env-file` flag and forward all other arguments unchanged.

Widespread renaming can miss references in docs or scripts.
Mitigation is to perform targeted repository search for `SQLCOMPARE_` and validate help/examples after edits.
