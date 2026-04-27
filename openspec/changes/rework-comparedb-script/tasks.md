## 1. Script Rename and Relocation

- [x] 1.1 Create root-level `comparedb.sh` from the existing wrapper behavior.
- [x] 1.2 Remove `scripts/run-demo.sh` after replacement references are updated.
- [x] 1.3 Rename wrapper variables and environment overrides to production-oriented names without `DEMO_` prefixes.
- [x] 1.4 Keep pass-through support for additional CLI arguments.
- [x] 1.5 Ensure `comparedb.sh` remains executable and uses strict shell options.

## 2. Env File Layout

- [x] 2.1 Rename `demo/sqlcomparer.env` to `demo/.env`.
- [x] 2.2 Add root `.env.TEMPLATE` with all supported SQL comparer dotenv keys and safe placeholder values.
- [x] 2.3 Update wrapper env default to `.env` in the current directory and require explicit table selection.
- [x] 2.4 Keep fixture credentials clearly labelled as fixture-only in the demo env file or docs.

## 3. Jar Handling

- [x] 3.1 Remove the `jar_needs_build` function and automatic Maven invocation from the wrapper.
- [x] 3.2 Check that required CLI jar artifacts are present before invoking Java.
- [x] 3.3 Print a clear build command such as `mvn -pl sqlcomparer-cli -am package` when jars are missing.
- [x] 3.4 Preserve `SQLCOMPARER_CLI_JAR` override support for non-default jar locations.

## 4. Documentation Updates

- [x] 4.1 Update README script references from `scripts/run-demo.sh` to `./comparedb.sh`.
- [x] 4.2 Update README env-file references from `demo/sqlcomparer.env` to `demo/.env`.
- [x] 4.3 Document `.env.TEMPLATE` as the starting point for user-managed configuration.
- [x] 4.4 Update README wording from demo wrapper to comparison wrapper or production script where appropriate.
- [x] 4.5 Update OpenSpec docs/specs or archived references that should point to current script names.
- [x] 4.6 Search for and remove stale references to `run-demo.sh`, `DEMO_ENV`, `DEMO_TABLES`, and `demo/sqlcomparer.env` in active docs and scripts.

## 5. Validation

- [x] 5.1 Run shell syntax checks for `comparedb.sh` and changed shell scripts.
- [x] 5.2 Run the relevant Maven tests or at least the CLI module test suite with reactor dependencies.
- [x] 5.3 Verify `comparedb.sh --help` describes the new env vars and paths.
- [x] 5.4 Verify the wrapper fails clearly when the CLI jar is absent or overridden to a missing path.
- [x] 5.5 Review OpenSpec status and ensure all tasks are complete.

## 6. Wrapper Default Refinement

- [x] 6.1 Change `comparedb.sh` to default to `.env` in the current directory.
- [x] 6.2 Remove the default tables file from `comparedb.sh`.
- [x] 6.3 Keep `SQLCOMPARER_TABLES_FILE` as an optional tables-file override.
- [x] 6.4 Update README fixture examples to pass `demo/.env` and `demo/tables.txt` explicitly.
- [x] 6.5 Update OpenSpec proposal, design, and specs to reflect current wrapper defaults.
- [x] 6.6 Validate shell syntax, help output, missing env handling, missing jar handling, and CLI module tests.
