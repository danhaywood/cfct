## Why

Documentation and environment-variable naming are inconsistent with current product identity and create confusion for both new users and maintainers.
A clearer README structure by audience and aligned `CFCT_` prefixes will reduce onboarding friction and configuration errors.

## What Changes

- Replace legacy `COMPAREDB_` wrapper environment-variable prefixes with `CFCT_`.
- Replace legacy `SQLCOMPARER_` and `SQLCOMPARE_` configuration/documentation prefixes with `CFCT_` across user-facing docs and examples.
- Reorganize README by audience, separating developer-focused content from user-focused content.
- Within user-focused content, split guidance into webapp usage and CLI usage sections with clearer task-oriented flow.
- Refresh and validate screenshots referenced by README so they reflect current UI behavior.
- Remove branding-logo screenshot requirements from README assets.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `demo-scripts-and-docs`: Update README structure, environment-variable naming guidance, and screenshot expectations.
- `cli-argument-driven-comparison`: Update documented dotenv key names from legacy prefixes to `CFCT_`.
- `webapp-playwright-connectivity-status`: Ensure screenshot baselines used for docs remain current with current UI state.

## Impact

This affects README structure and examples, wrapper environment-variable documentation, and CLI dotenv key documentation.
This affects demo and template env files that are referenced by users.
This affects screenshot assets and possibly Playwright screenshot update assertions.
No comparison logic or output semantics are changed.
