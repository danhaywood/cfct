## 1. Script Rename and Relocation

- [ ] 1.1 Create root-level `comparedb.sh` from the existing wrapper behavior.
- [ ] 1.2 Remove `scripts/run-demo.sh` after replacement references are updated.
- [ ] 1.3 Rename wrapper variables and environment overrides to production-oriented names without `DEMO_` prefixes.
- [ ] 1.4 Keep pass-through support for additional CLI arguments.
- [ ] 1.5 Ensure `comparedb.sh` remains executable and uses strict shell options.

## 2. Env File Layout

- [ ] 2.1 Rename `demo/sqlcomparer.env` to `demo/.env`.
- [ ] 2.2 Add root `.env.TEMPLATE` with all supported SQL comparer dotenv keys and safe placeholder values.
- [ ] 2.3 Update wrapper defaults to use `demo/.env` and `demo/tables.txt`.
- [ ] 2.4 Keep fixture credentials clearly labelled as fixture-only in the demo env file or docs.

## 3. Jar Handling

- [ ] 3.1 Remove the `jar_needs_build` function and automatic Maven invocation from the wrapper.
- [ ] 3.2 Check that required CLI jar artifacts are present before invoking Java.
- [ ] 3.3 Print a clear build command such as `mvn -pl sqlcomparer-cli -am package` when jars are missing.
- [ ] 3.4 Preserve `SQLCOMPARER_CLI_JAR` override support for non-default jar locations.

## 4. Documentation Updates

- [ ] 4.1 Update README script references from `scripts/run-demo.sh` to `./comparedb.sh`.
- [ ] 4.2 Update README env-file references from `demo/sqlcomparer.env` to `demo/.env`.
- [ ] 4.3 Document `.env.TEMPLATE` as the starting point for user-managed configuration.
- [ ] 4.4 Update README wording from demo wrapper to comparison wrapper or production script where appropriate.
- [ ] 4.5 Update OpenSpec docs/specs or archived references that should point to current script names.
- [ ] 4.6 Search for and remove stale references to `run-demo.sh`, `DEMO_ENV`, `DEMO_TABLES`, and `demo/sqlcomparer.env` in active docs and scripts.

## 5. Validation

- [ ] 5.1 Run shell syntax checks for `comparedb.sh` and changed shell scripts.
- [ ] 5.2 Run the relevant Maven tests or at least the CLI module test suite with reactor dependencies.
- [ ] 5.3 Verify `comparedb.sh --help` describes the new env vars and paths.
- [ ] 5.4 Verify the wrapper fails clearly when the CLI jar is absent or overridden to a missing path.
- [ ] 5.5 Review OpenSpec status and ensure all tasks are complete.
