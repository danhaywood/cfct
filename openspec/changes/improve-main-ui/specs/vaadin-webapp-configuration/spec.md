## ADDED Requirements

### Requirement: Home page footer surfaces configured connection context
The webapp home page SHALL display configured connection context in a footer.
The footer SHALL read its displayed values from the same typed configuration properties used by the webapp startup and connectivity validation paths.
The footer SHALL display the SQL Server identity, left database name, and right database name.
The footer SHALL omit or mask sensitive credential values.

#### Scenario: Footer uses configured properties
- **WHEN** the webapp starts with configured SQL Server and database values
- **THEN** the home page footer displays those configured connection values

#### Scenario: Footer protects credentials
- **WHEN** the home page footer displays configured connection context
- **THEN** sensitive credential values are omitted or masked
