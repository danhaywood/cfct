## Why

The CLI now supports `.env` and table-file inputs, but the repository does not yet provide a low-friction demo path that shows users how to run a comparison against the fixture data.
A wrapper script, fixture lifecycle script, demo input files, and refreshed README will make the project easier to try from a clean checkout and will reduce drift between documented and actual CLI behavior.

## What Changes

- Add a shell wrapper script for invoking the SQL comparer CLI with demo-friendly defaults.
- Add a shell script for starting and stopping the fixture SQL Server environment used by the demo.
- Add a demo `.env` file containing non-production connection values for the fixture SQL Server.
- Add a demo tables flat file with one table per line.
- Update the README with current build, test, fixture, and CLI demo instructions.
- Correct README content that is out of date with the current project behavior.

## Capabilities

### New Capabilities
- `demo-scripts-and-docs`: Provides runnable demo scripts, demo input files, and documentation for starting the fixture SQL Server and running the CLI against it.

### Modified Capabilities

## Impact

- Adds repository-level scripts and demo files.
- Updates README documentation.
- May add lightweight shell-script checks if the project has an existing validation pattern for scripts.
- Does not change the core comparison API, report formats, or SQL comparison semantics.
