## Why

The project needs a web application entry point so users can configure comparison runs without passing many CLI arguments each time.
We should scaffold the Vaadin application and configuration binding now so future UI work can focus on workflows instead of platform setup.

## What Changes

- Add a new `sqlcomparer-webapp` Maven module with Spring Boot and Vaadin scaffolding.
- Use Vaadin Flow latest stable line from Context7 metadata, selecting `24.9.2` and avoiding `25.0.0-beta1` pre-release.
- Add baseline `application.yml` configuration keys that mirror the CLI connection and output options.
- Add typed Spring configuration properties classes for server, username, password, left database, right database, tables/tables-file, env-file, output-format, and output-file.
- Define precedence and mapping rules so webapp configuration sources can resolve the same logical inputs the CLI accepts.
- Keep this change focused on scaffolding and configuration only, without implementing comparison UI screens.

## Capabilities

### New Capabilities
- `vaadin-webapp-configuration`: Provide Vaadin webapp scaffolding and configuration binding for comparison settings equivalent to CLI inputs.

### Modified Capabilities
- `maven-multi-module-structure`: Extend the reactor module list and layering rules to include a webapp module.

## Impact

This change affects root Maven module declarations, a new `sqlcomparer-webapp` module, Spring Boot configuration files, and documentation for running the webapp.
This change introduces Vaadin dependencies and application configuration surface area but does not change comparison engine behavior.
