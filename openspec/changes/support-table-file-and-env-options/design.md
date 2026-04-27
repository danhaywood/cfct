## Context

The CLI currently parses a fixed set of flags in `CliArgumentsParser` and requires `-S`, `-U`, `-P`, `-l`, `-r`, and `-t` to be present on every invocation.
The table list is currently parsed only from a comma-separated `schema.table` value passed to `-t`.
Connection values can include sensitive credentials, so requiring the password on every command line is inconvenient and can expose it through shell history or process listings.
The change should stay within the CLI module and should not change the core comparison APIs.

## Goals / Non-Goals

**Goals:**

- Support a flat table file with one `schema.table` table reference per line.
- Preserve the existing comma-separated `-t` behavior for backward-compatible command-line usage.
- Load default values for `-S`, `-U`, `-P`, `-l`, and `-r` from a `.env` file when those flags are not supplied.
- Make CLI flags take precedence over `.env` values.
- Keep validation deterministic and easy to test.

**Non-Goals:**

- Do not introduce a new comparison request model in the core library.
- Do not make table selections configurable through `.env` in this change.
- Do not add support for comments, wildcards, or table groups in the table flat file unless explicitly specified by requirements.
- Do not require users to provide a `.env` file when all connection values are supplied on the command line.

## Decisions

- Add a dedicated table-file option, preferably `--tables-file`, rather than overloading `-t` with path detection.
  This keeps the existing `-t` semantics unambiguous and avoids treating a real table name as a file path by mistake.
  Alternative considered: overload `-t` so `-t @file` or `-t path` reads a file, but that creates ambiguous parsing and less obvious validation messages.

- Parse table files using the same `schema.table` validation used for `-t` tokens.
  Each non-blank line becomes one table reference and input order is preserved.
  Alternative considered: implement a separate parser for file input, but shared parsing reduces divergent behavior.

- Require exactly one table source: either `-t` or `--tables-file`.
  Supplying both should fail with a clear validation error because there is no obvious merge precedence and duplicate handling would become surprising.
  Alternative considered: concatenate command-line tables and file tables, but that makes order and duplicate semantics harder to explain.

- Implement `.env` loading as a small internal parser in the CLI module rather than adding a dependency unless implementation discovers an existing project-approved library.
  The required format can be limited to simple `KEY=value` entries, blank lines, and `#` comments, which is sufficient for the five connection settings.
  Alternative considered: add a dotenv dependency, but the feature scope is small and avoiding a dependency keeps the CLI simpler.

- Use stable, documented `.env` key names that correspond to the existing flags: `SQLCOMPARER_SERVER`, `SQLCOMPARER_USERNAME`, `SQLCOMPARER_PASSWORD`, `SQLCOMPARER_LEFT_DATABASE`, and `SQLCOMPARER_RIGHT_DATABASE`.
  Alternative considered: use short names like `S`, `U`, `P`, `L`, and `R`, but explicit names are easier to understand and less likely to conflict with other environment conventions.

- Load `.env` from the current working directory by default, with an optional `--env-file` path to support scripts that keep configuration elsewhere.
  CLI flags still override values from the selected `.env` file.
  Alternative considered: only support a default `.env`, but an explicit path makes tests and CI usage easier without changing the main workflow.

## Risks / Trade-offs

- `.env` parsing could accidentally accept more syntax than intended → Keep the supported syntax small and cover it with unit tests.
- Users may expect shell environment variables to be read as well as `.env` values → Document and test only `.env` behavior for this change unless implementation explicitly expands scope.
- A default `.env` file may contain secrets → Avoid logging loaded secret values and include `.env` in normal local-secret handling if not already ignored.
- Existing tests may assume all six original flags are always required → Update tests to assert resolved required values rather than raw argument presence.
