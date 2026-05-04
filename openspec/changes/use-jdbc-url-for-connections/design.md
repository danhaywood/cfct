## Context

CFCT currently models SQL Server connection input around a `server` host:port value plus separate credentials and database names.
This works for local fixture usage but is too limited for deployment targets that require additional JDBC URL parameters.
Azure SQL Managed Instance and similar environments often need connection properties such as encryption, host certificate settings, login timeout, and application intent encoded in the URL.
The change touches CLI arguments, dotenv defaults, webapp login/config defaults, and connection construction in implementation code.

## Goals / Non-Goals

**Goals:**
- Replace server-host style input with JDBC URL input across CLI and webapp comparison entry paths.
- Preserve current authentication and left/right database semantics while allowing deployers to supply URL-level SQL Server options.
- Keep fixture and local usage straightforward by documenting valid local JDBC URL examples.
- Provide clear migration guidance for existing users moving from `server` input to JDBC URL input.

**Non-Goals:**
- No change to comparison algorithms or result rendering behavior.
- No introduction of new authentication mechanisms beyond existing username/password handling.
- No attempt to auto-convert all historical saved configs at runtime beyond documented key/argument replacement.

## Decisions

### Decision: Introduce `jdbc-url` as the canonical connection input.
CLI and webapp configuration/login defaults will use JDBC URL terminology and values.
This removes ambiguity and gives deployers direct control over SQL Server driver parameters.
Alternative considered: keep `server` and add optional advanced URL options field.
Rejected because dual models create conflicting precedence rules and more user confusion.

### Decision: Continue selecting left/right databases independently from URL.
Keep `left-database` and `right-database` as separate inputs while treating JDBC URL as the connection endpoint/options base.
This preserves existing two-database workflow and minimizes blast radius in comparison orchestration.
Alternative considered: require database name inside each JDBC URL and remove left/right database fields.
Rejected because it complicates current UX and requires larger changes across CLI/webapp flows.

### Decision: Use SQL Server JDBC URL examples in docs and fixture defaults.
Update `.env` examples, README snippets, and webapp config defaults to show executable `jdbc:sqlserver://...` values.
This ensures new deployers adopt the supported model immediately.
Alternative considered: keep legacy examples and add a migration note.
Rejected because examples strongly shape usage and would prolong server-based assumptions.

### Decision: Treat server-based keys/arguments as breaking and remove them from current contract.
Spec and docs will define `jdbc-url` as required instead of `server`.
This keeps the contract explicit and avoids hidden fallback behavior that is hard to test.
Alternative considered: temporary compatibility alias from `server` to synthesized URL.
Rejected to avoid silent misconfiguration for deployments requiring explicit URL properties.

## Risks / Trade-offs

- [Existing automation breaks due to renamed keys/args] → Provide explicit migration notes and update scripts/docs in the same change.
- [Users provide malformed JDBC URLs] → Validate required SQL Server URL prefix and fail with clear input error messages.
- [Fixture/docs drift from actual parser expectations] → Keep examples aligned with tested CLI/webapp happy-path commands.
- [Confusion about where database name belongs] → Document that left/right database fields remain separate from JDBC URL endpoint/options.

## Migration Plan

Update specs, docs, and defaults to use JDBC URL keys and arguments.
Implement parsing/validation and connection construction changes in CLI and webapp paths.
Update tests and demo scripts to use JDBC URL examples.
Communicate breaking rename from `server` to `jdbc-url` in README and change notes.

## Open Questions

Should CLI accept both `--jdbc-url` and a short alias such as `-J`, or only long-form for clarity.
Should we support temporary one-release compatibility for legacy `CFCT_SERVER` env keys.
