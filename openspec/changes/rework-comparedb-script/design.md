## Context

The project currently has `scripts/run-demo.sh`, a wrapper that is described as a demo script and defaults to `demo/sqlcomparer.env` and `demo/tables.txt`.
The wrapper also contains `jar_needs_build` logic that can invoke Maven automatically when it thinks the CLI jar is stale or missing.
The user wants this wrapper to be production-oriented, repository-root visible, and explicit about requiring built jar artifacts before it runs.

## Goals / Non-Goals

**Goals:**

- Replace `scripts/run-demo.sh` with a root-level `comparedb.sh` script.
- Rename script variables and environment overrides to production-oriented names such as `SQLCOMPARER_ENV_FILE`, `SQLCOMPARER_TABLES_FILE`, and `SQLCOMPARER_CLI_JAR`.
- Rename `demo/sqlcomparer.env` to `demo/.env` for the fixture example.
- Add a root `.env.TEMPLATE` that documents required dotenv keys without implying fixture-only usage.
- Remove automatic Maven build and stale-jar detection from the wrapper.
- Fail clearly when the CLI jar is not present and instruct the user to build it.
- Update README and relevant OpenSpec docs/specs to match the new script name and file paths.

**Non-Goals:**

- Do not change CLI Java behavior or argument names.
- Do not change the fixture SQL Server lifecycle script semantics.
- Do not remove the `demo/` fixture data or `demo/tables.txt` example.
- Do not add cross-platform Windows batch or PowerShell scripts in this change.

## Decisions

- Place `comparedb.sh` in the repository root.
  This makes the primary comparison wrapper discoverable and convenient for production-style use.
  Alternative considered: keep it in `scripts/`, but that reinforces the helper/demo framing the user wants to remove.

- Keep `demo/.env` as the fixture-specific env file and add `.env.TEMPLATE` at the root for production configuration.
  The fixture file remains a runnable example, while the template gives users a safe starting point for real environments.
  Alternative considered: make root `.env` the default, but committing a root `.env` with fixture credentials would be misleading and unsafe.

- Default `comparedb.sh` to `demo/.env` and `demo/tables.txt` only as repository examples, while allowing overrides through production-named environment variables.
  This preserves a quick local path without encoding demo naming into the wrapper itself.
  Alternative considered: require all paths on every invocation, but that would make the wrapper less useful.

- Remove `jar_needs_build` and all automatic Maven invocation from the wrapper.
  The wrapper should verify the executable jar exists and fail with a clear build command if it does not.
  Alternative considered: keep auto-build with renamed functions, but the user explicitly asked to remove that behavior.

- Update README references from “demo wrapper” to “comparison wrapper” and document the fixture as an example setup rather than the script identity.
  This aligns docs with production-oriented naming while preserving fixture instructions.

## Risks / Trade-offs

- Users may expect the wrapper to build automatically because `run-demo.sh` did → Print an explicit build command when the jar is missing.
- Moving the script can leave stale references behind → Search README, scripts, OpenSpec docs, and shell examples for `run-demo.sh`, `DEMO_ENV`, `DEMO_TABLES`, and `demo/sqlcomparer.env`.
- Root `.env.TEMPLATE` could be confused with active config → Use template comments and keep active fixture config in `demo/.env`.
- Dotfiles under `demo/` can be overlooked by basic directory listings → README must mention the exact `demo/.env` path.
