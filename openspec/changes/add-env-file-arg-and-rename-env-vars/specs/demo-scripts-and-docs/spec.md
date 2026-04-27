## MODIFIED Requirements

### Requirement: Repository provides a CLI demo wrapper script
The repository SHALL provide a root-level shell script named `comparedb.sh` that invokes the SQL comparer CLI using configurable env-file and table-file inputs.
The wrapper SHALL verify that the CLI executable artifact is present before invoking it.
The wrapper SHALL fail with a clear build instruction when required jar artifacts are missing.
The wrapper SHALL not automatically build or rebuild jar artifacts.
The wrapper SHALL use production-oriented variable names and environment overrides rather than `DEMO_`-prefixed names.
The wrapper SHALL use `.env` in the current working directory as its default env file.
The wrapper SHALL not define a default tables file.
The wrapper SHALL support table selection by pass-through CLI arguments or by `COMPAREDB_TABLES_FILE`.
The wrapper SHALL allow callers to pass additional CLI arguments without editing the script.
The wrapper SHALL support env-file selection via `--env-file <path>`.
The wrapper SHALL support env-file selection via `COMPAREDB_ENV_FILE`.
The wrapper SHALL prioritize `--env-file <path>` over `COMPAREDB_ENV_FILE` when both are provided.

#### Scenario: Comparison wrapper runs with explicit fixture example inputs
- **WHEN** the fixture SQL Server is running, required jars are built, and the user runs `./comparedb.sh` with `COMPAREDB_ENV_FILE=demo/.env` and `--tables-file demo/tables.txt`
- **THEN** the wrapper invokes the CLI with `demo/.env` and `demo/tables.txt`

#### Scenario: Comparison wrapper accepts additional arguments
- **WHEN** the user runs `./comparedb.sh` with additional CLI arguments
- **THEN** the wrapper passes those arguments through to the CLI invocation

#### Scenario: Comparison wrapper supports tables file environment override
- **WHEN** the user runs `./comparedb.sh` with `COMPAREDB_TABLES_FILE` set
- **THEN** the wrapper passes that file path to the CLI table-file option

#### Scenario: Comparison wrapper supports env-file argument override
- **WHEN** the user runs `./comparedb.sh --env-file custom.env --tables-file demo/tables.txt`
- **THEN** the wrapper invokes the CLI using `custom.env` as the env file input

#### Scenario: Comparison wrapper prioritizes explicit env-file argument
- **WHEN** the user runs `./comparedb.sh --env-file custom.env` with `COMPAREDB_ENV_FILE=demo/.env`
- **THEN** the wrapper invokes the CLI using `custom.env` instead of `demo/.env`

#### Scenario: Comparison wrapper reports missing jar artifacts
- **WHEN** the user runs `./comparedb.sh` before required jar artifacts have been built
- **THEN** the wrapper exits with a non-zero status and reports the Maven build command needed before retrying
