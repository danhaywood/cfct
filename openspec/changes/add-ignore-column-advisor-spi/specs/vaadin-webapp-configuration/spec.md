## MODIFIED Requirements

### Requirement: Webapp configuration models the same logical inputs as CLI
The webapp SHALL provide typed configuration properties for login defaults and execution preferences such as server, database names, username, password, env-file path, output format, and output file.
The webapp SHALL provide typed configuration properties for default ignore-column advisor enablement flags.
The webapp SHALL allow independent enable or disable control for identity, uuid/guid, and timestamp ignore advisors.
The webapp SHALL default each default ignore-column advisor enablement flag to enabled.
The webapp SHALL use configured property values only as initial login defaults and SHALL allow users to edit any field before authentication.
The webapp SHALL NOT require these properties to be present for interactive webapp use.
The webapp SHALL document how runtime login inputs and configuration defaults map to equivalent CLI argument concepts.
The webapp SHALL treat table selection as a strategy concern and SHALL NOT require parity with CLI table-input flags.

#### Scenario: Webapp starts without configured credentials
- **WHEN** the webapp starts without username or password values in `application.yml`
- **THEN** typed configuration still binds non-secret defaults and the application starts successfully

#### Scenario: Configured defaults pre-populate login form
- **WHEN** the webapp has connection-related properties configured in `application.yml` or externalized configuration
- **THEN** those values are shown as initial editable defaults in the login form

#### Scenario: Configuration defaults can be overridden externally
- **WHEN** a deploy environment provides overriding Spring configuration values for login defaults
- **THEN** the webapp resolves those values over defaults from `application.yml`

#### Scenario: Ignore-column advisors are enabled by default
- **WHEN** no explicit ignore-advisor enablement values are provided
- **THEN** identity, uuid/guid, and timestamp ignore advisors remain enabled

#### Scenario: One ignore-column advisor can be disabled independently
- **WHEN** deployment configuration disables one ignore-column advisor flag and leaves others enabled
- **THEN** only that advisor stops contributing ignore decisions while other advisors continue to apply
