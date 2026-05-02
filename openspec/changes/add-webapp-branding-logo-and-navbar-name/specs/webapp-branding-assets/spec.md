## ADDED Requirements

### Requirement: Webapp provides static branding asset resources
The webapp SHALL include a committed logo/icon asset in classpath resources for UI branding usage.
The branding asset SHALL be available to both unauthenticated and authenticated UI views.
The branding asset path SHALL be stable for deterministic tests.

#### Scenario: Branding asset is available in webapp resources
- **WHEN** the webapp module is built and resources are packaged
- **THEN** the logo/icon asset exists in classpath resources and can be referenced by UI components

### Requirement: Webapp reuses shared brand identity across login and main shell
The webapp SHALL use the same product branding identity in the login experience and the main shell.
The login view SHALL render a larger branding presentation.
The main shell SHALL render a compact branding presentation suitable for the navbar.

#### Scenario: Brand identity is consistent between login and navbar
- **WHEN** a user views the login experience and then the authenticated main shell
- **THEN** both views show the same product identity using the shared logo/icon and `CFCT` naming
