## 1. Update CLI connection contract to JDBC URL

- [x] 1.1 Replace CLI connection argument parsing from `-S/--server` to `--jdbc-url`, including required-argument validation and help text.
- [x] 1.2 Update CLI dotenv resolution to use `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_DRIVER_CLASS_NAME`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`, keeping override precedence rules unchanged.
- [x] 1.3 Update CLI connection construction to use JDBC URL input plus existing username/password and left/right database selection.
- [x] 1.4 Update CLI tests for argument parsing, dotenv loading, and connection validation scenarios to assert JDBC URL behavior.

## 2. Update webapp configuration and login input semantics

- [x] 2.1 Replace webapp typed custom connection defaults with Spring datasource properties (`spring.datasource.url`, `spring.datasource.driver-class-name`, `spring.datasource.username`, `spring.datasource.password`).
- [x] 2.2 Update login form labels/placeholders and request handling to capture JDBC URL input while preserving editable defaults.
- [x] 2.3 Update webapp connectivity-validation and comparison execution wiring to consume JDBC URL input correctly.
- [x] 2.4 Update webapp tests for login defaults, validation success/failure, and comparison execution paths using JDBC URL input.

## 3. Update docs, examples, and scripts

- [x] 3.1 Update `README.adoc` CLI and webapp configuration sections to document JDBC URL usage, including Azure SQL MI-style examples with URL parameters.
- [x] 3.2 Update `demo/.env`, `.env.TEMPLATE`, and related script expectations from `CFCT_SERVER`/`CFCT_JDBC_URL` to `SPRING_DATASOURCE_*` plus `CFCT_LEFT_DATABASE` and `CFCT_RIGHT_DATABASE`.
- [x] 3.3 Update wrapper and happy-path script docs/examples to pass JDBC URL-based settings.

## 4. Verify end-to-end behavior and migration messaging

- [x] 4.1 Run module tests affected by CLI and webapp connection input changes and fix regressions.
- [x] 4.2 Verify fixture-based happy-path commands still execute with JDBC URL examples.
- [x] 4.3 Add explicit migration notes in README for breaking rename from server/custom CFCT connection inputs to Spring datasource-based inputs.