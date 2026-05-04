## REMOVED Requirements

### Requirement: Webapp resolves table targets through SelectionPlan strategies
**Reason**: The SelectionPlan path is unused in production and has been removed to reduce dead runtime wiring.
**Migration**: Use the existing manual table selection and command-driven table inference flows in the webapp UI.
