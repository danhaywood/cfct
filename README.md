# sqlcomparer

`sqlcomparer` is a Maven multi-module project for comparing selected SQL Server tables between two databases.
It provides a reusable comparison API, an implementation module, a Spring Boot CLI, a root comparison wrapper script, and a Docker-backed integration-test fixture.

## Project layout

- `comparedb.sh`: root comparison wrapper script for running the CLI with env-file and table-selection inputs.
- `.env.TEMPLATE`: template for user-managed connection configuration.
- `sqlcomparer-api`: public comparison contracts, result models, exceptions, and service interfaces.
- `sqlcomparer-impl`: comparison services, SQL Server readers, JSON request loading, and report renderers.
- `sqlcomparer-cli`: Spring Boot CLI application and executable jar packaging.
- `sqlcomparer-integration-tests`: SQL Server Testcontainers harness, fixture SQL, approval files, and integration tests.
- `demo/`: committed fixture example files for local comparison runs.
- `scripts/`: local helper scripts for the fixture SQL Server.

## Prerequisites

- Java 17 or later.
- Maven 3.9 or later.
- Docker, for the fixture SQL Server and integration tests.
- Enough local resources and startup time for the `mcr.microsoft.com/mssql/server:2022-latest` container.

SQL Server container startup can be slow, especially on Apple Silicon where emulation may be involved.
The committed fixture credentials are examples for the local fixture only and are not production-safe.
Do not reuse them for real systems.

## Build and test

Run the non-integration test suite from the repository root:

```bash
mvn test
```

Run the full build, including SQL Server integration tests, from a Docker-enabled environment:

```bash
mvn verify
```

Build the CLI jar and its required reactor modules before using `comparedb.sh`:

```bash
mvn -pl sqlcomparer-cli -am package
```

`comparedb.sh` does not build or rebuild jars automatically.
If the CLI jar is missing, it prints the build command and exits.

## Fixture SQL Server

Use `scripts/fixture-sqlserver.sh` to manage a local SQL Server container for the fixture example.
The script starts SQL Server 2022, creates `left_db` and `right_db`, and loads fixture data for the example tables.

Start the fixture:

```bash
scripts/fixture-sqlserver.sh start
```

Check fixture status:

```bash
scripts/fixture-sqlserver.sh status
```

Stop and remove the fixture:

```bash
scripts/fixture-sqlserver.sh stop
```

By default the fixture listens on `localhost:14333` and uses the container name `sqlcomparer-fixture-sqlserver`.
Override the host port if needed:

```bash
SQLCOMPARER_FIXTURE_PORT=14334 scripts/fixture-sqlserver.sh start
```

If you change the port, also update the env file used by the comparison wrapper or pass a different env file with `SQLCOMPARER_ENV_FILE`.

## Comparison wrapper

`comparedb.sh` is the root comparison wrapper script.
It expects the CLI jar to already exist and then invokes the Java CLI with an env file.
By default, the env file is `.env` in the current directory.
The wrapper has no default tables file; provide table selection by passing `--tables-file`, passing `-t`, or setting `SQLCOMPARER_TABLES_FILE`.

For the fixture example, build the CLI jar, start the fixture, then run:

```bash
SQLCOMPARER_ENV_FILE=demo/.env ./comparedb.sh --tables-file demo/tables.txt
```

Equivalent usage with the tables-file environment override:

```bash
SQLCOMPARER_ENV_FILE=demo/.env \
SQLCOMPARER_TABLES_FILE=demo/tables.txt \
./comparedb.sh
```

This overrides the server value from the env file:

```bash
SQLCOMPARER_ENV_FILE=demo/.env ./comparedb.sh --tables-file demo/tables.txt -S localhost:14334
```

This writes JSON output instead of the default text output:

```bash
SQLCOMPARER_ENV_FILE=demo/.env ./comparedb.sh --tables-file demo/tables.txt --output-format json
```

This writes JSON output to a file:

```bash
SQLCOMPARER_ENV_FILE=demo/.env ./comparedb.sh --tables-file demo/tables.txt --output-format json -o comparison.json
```

This writes Excel output to a workbook file:

```bash
SQLCOMPARER_ENV_FILE=demo/.env ./comparedb.sh --tables-file demo/tables.txt --output-format excel -o comparison.xlsx
```

The wrapper supports these environment overrides:

- `SQLCOMPARER_ENV_FILE`: env file path, defaulting to `.env` in the current directory.
- `SQLCOMPARER_TABLES_FILE`: optional table list file path, with no default.
- `SQLCOMPARER_CLI_JAR`: CLI jar path, defaulting to `sqlcomparer-cli/target/sqlcomparer-cli-0.0.1-SNAPSHOT.jar`.

## Env files and table files

Use `.env.TEMPLATE` as the starting point for a user-managed env file.
Copy it to `.env` in the directory from which you run `comparedb.sh`, or store it elsewhere and set `SQLCOMPARER_ENV_FILE`.
Do not commit real production credentials.

`demo/.env` contains fixture-only connection values using the CLI-supported dotenv keys:

```dotenv
SQLCOMPARER_SERVER=localhost:14333
SQLCOMPARER_USERNAME=sa
SQLCOMPARER_PASSWORD=Str0ng_password!123
SQLCOMPARER_LEFT_DATABASE=left_db
SQLCOMPARER_RIGHT_DATABASE=right_db
```

`demo/tables.txt` contains one table reference per line:

```text
dbo.Supplier
dbo.Product
dbo.PurchaseOrder
```

These demo files are examples for the local fixture only.
Do not put production credentials in committed demo files.

## Direct CLI usage

You can run the CLI jar directly after building it:

```bash
java -jar sqlcomparer-cli/target/sqlcomparer-cli-0.0.1-SNAPSHOT.jar \
  -S localhost:14333 \
  -U sa \
  -P 'Str0ng_password!123' \
  -l left_db \
  -r right_db \
  -t dbo.Supplier,dbo.Product,dbo.PurchaseOrder \
  --output-format text
```

The CLI supports these connection options:

- `-S`: SQL Server host, optionally including a port such as `localhost:14333`.
- `-U`: SQL Server username.
- `-P`: SQL Server password.
- `-l`: left database name.
- `-r`: right database name.

The CLI supports these table-selection options:

- `-t`: comma-separated `schema.table` list.
- `--tables-file`: path to a flat file with one `schema.table` reference per line.

The CLI supports this dotenv option:

- `--env-file`: path to a file containing `SQLCOMPARER_SERVER`, `SQLCOMPARER_USERNAME`, `SQLCOMPARER_PASSWORD`, `SQLCOMPARER_LEFT_DATABASE`, and `SQLCOMPARER_RIGHT_DATABASE`.

The CLI supports these output options:

- `--output-format`: one of `text`, `json`, or `excel`.
- `-o`: optional output file path for successful output.

`text` is the default when `--output-format` is omitted.
Text and JSON are written to stdout as UTF-8 when `-o` is omitted.
When `-o` is supplied, successful output is written to that file instead.
Excel output requires `-o`, for example `--output-format excel -o comparison.xlsx`.

Explicit CLI values override values loaded from `--env-file`.
Use either `-t` or `--tables-file`, not both.

## Testing conventions

Use AssertJ for fluent assertions in harness tests.
Use JUnit 5 parameterized tests with `@EnumSource` when the same behavior must be checked across the left and right logical databases or similar modes.
Use Approvals for stable textual, JSON, Excel, or tabular outputs when characterization-style verification is clearer than many small assertions.

## Scope guardrail

The fixture scripts and integration harness are intentionally narrow.
They own local/example SQL Server lifecycle, logical database creation, fixture initialization, and smoke-test setup.
They do not implement comparison logic, reporting logic, or a broader database support matrix.
