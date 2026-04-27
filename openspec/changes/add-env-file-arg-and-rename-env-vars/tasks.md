## 1. Wrapper argument handling

- [ ] 1.1 Add `--env-file <path>` parsing to `comparedb.sh` while preserving pass-through behavior for other CLI arguments.
- [ ] 1.2 Implement precedence so `--env-file` overrides `COMPAREDB_ENV_FILE`, and `COMPAREDB_ENV_FILE` overrides the default `.env` path.
- [ ] 1.3 Ensure wrapper help output documents `--env-file` usage and precedence clearly.

## 2. Environment variable rename

- [ ] 2.1 Rename wrapper environment variable references from `SQLCOMPARE_*` to `COMPAREDB_*` in `comparedb.sh`.
- [ ] 2.2 Update any script-level examples, comments, and usage strings to remove `SQLCOMPARE_*` names.
- [ ] 2.3 Search active scripts and docs for `SQLCOMPARE_` references and replace with `COMPAREDB_` where applicable.

## 3. Documentation and spec alignment

- [ ] 3.1 Update README wrapper examples to show `--env-file` and `COMPAREDB_*` usage.
- [ ] 3.2 Update OpenSpec change artifacts if needed so wording matches implemented variable names and argument behavior.

## 4. Validation

- [ ] 4.1 Run shell syntax checks for modified scripts.
- [ ] 4.2 Verify `./comparedb.sh --help` includes `--env-file` and `COMPAREDB_*` references.
- [ ] 4.3 Verify wrapper behavior for env-file selection precedence with argument, env var, and default cases.
