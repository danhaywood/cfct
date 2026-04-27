## Context

The current CLI module only boots a Spring context and does not execute comparisons from command-line input.
Comparison behavior already exists in the library layer through multi-table comparison services and deterministic report rendering.
Users need a first-pass executable workflow that can be run directly with SQL Server connection and table-selection arguments.

## Goals / Non-Goals

**Goals:**

- Add a CLI entry path that accepts `-S`, `-U`, `-P`, `-l`, `-r`, and `-t` arguments.
- Parse `-t` as an ordered comma-separated list of `schema.table` tokens.
- Open left and right JDBC connections against the same server instance but different database names.
- Invoke existing library comparison services and print deterministic output to stdout.
- Add focused tests for argument validation, table parsing, service invocation, and user-visible failures.

**Non-Goals:**

- No interactive prompts.
- No secure secret store integration in this first pass.
- No JSON request-file input path for this CLI feature.
- No change to core comparison semantics.

## Decisions

- Implement a dedicated CLI runner component that is called from `main` with raw args.
Alternative considered: parse args directly in `main`.
A dedicated component is easier to unit-test.

- Keep first-pass parsing simple and explicit, with required flags and clear validation errors for missing values.
Alternative considered: introducing a full command framework.
Manual parsing keeps dependencies low and scope tight.

- Require table tokens in `schema.table` format and reject malformed or empty tokens.
Alternative considered: default schema inference.
Explicit format avoids ambiguity.

- Build SQL Server JDBC URLs per side using shared server and credentials with separate database names.
Alternative considered: separate server arguments per side.
Shared server matches the requested first-pass contract.

- Return non-zero exit on validation or execution failure and print a concise error message to stderr.
Alternative considered: swallowing exceptions with success exit.
Non-zero exit is required for scriptability.

## Risks / Trade-offs

- [Risk] Password in command-line args can leak via process inspection.
→ Mitigation: document this as a first-pass limitation and isolate credential handling in one place for future hardening.

- [Risk] Manual argument parsing can regress as options grow.
→ Mitigation: cover parser behavior with comprehensive unit tests and clear error cases.

- [Risk] Direct JDBC connection management can introduce resource leaks.
→ Mitigation: use try-with-resources for both connections and keep connection creation in a small helper.
