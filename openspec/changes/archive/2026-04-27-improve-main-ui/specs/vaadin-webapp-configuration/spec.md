## ADDED Requirements

### Requirement: Home page footer surfaces configured connection context
The webapp home page SHALL display configured connection context and SQL connectivity status in a fixed footer/status bar.
The footer/status bar SHALL read its displayed values from the same typed configuration properties used by the webapp startup and connectivity validation paths.
The footer/status bar SHALL display the SQL Server identity, left database name, right database name, and current SQL connectivity status.
The footer/status bar SHALL omit or mask sensitive credential values.

#### Scenario: Footer uses configured properties
- **WHEN** the webapp starts with configured SQL Server and database values
- **THEN** the home page footer/status bar displays those configured connection values and SQL connectivity status

#### Scenario: Footer protects credentials
- **WHEN** the home page footer/status bar displays configured connection context
- **THEN** sensitive credential values are omitted or masked
