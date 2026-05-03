## Why

The project needs a web application entry point so users can configure comparison runs without passing many CLI arguments each time.
We should scaffold the Vaadin application and configuration binding now so future UI work can focus on workflows instead of platform setup.

## What Changes

- Add a new `cfct-webapp` Maven module with Spring Boot and Vaadin scaffolding.
- Use Vaadin Flow latest stable 25.x line for Spring Boot 4 compatibility and avoid pre-release versions.
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

This change affects root Maven module declarations, a new `cfct-webapp` module, Spring Boot configuration files, and documentation for running the webapp.
This change introduces Vaadin dependencies and application configuration surface area but does not change comparison engine behavior.
