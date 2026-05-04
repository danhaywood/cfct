## Why

Deployers can start the fixture and webapp but still miss database prerequisites that only surface after validation or comparison failures.
The README should make real-world prerequisites explicit up front so deployers can prepare production-like databases before first use.

## What Changes

- Add a concise README section for deployers that lists the three required target-database system tables or views used by CFCT validation.
- Add README detail for required system-object structure, including expected column names and SQL Server data types.
- Add README guidance that compared business tables must expose a `_PK`-suffixed unique index or unique constraint so CFCT can resolve row identity.
- Add a README configuration reference section in `application.yml` format covering supported runtime properties for webapp defaults and comparison execution.
- Add a configuration property table that explains each property, default value, and purpose.
- Place this guidance near existing fixture and webapp setup steps so deployers see prerequisites before running comparisons.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `demo-scripts-and-docs`: Extend README documentation requirements to explicitly describe required target system objects, `_PK` comparability prerequisites, and an `application.yml`-formatted configuration reference.

## Impact

README content and structure are updated to include an explicit database requirements section.
No runtime code, API contracts, or dependencies change.
