## Why

The webapp currently lacks visible product branding, which makes the login and main shell feel generic.
Adding a logo and CFCT naming improves recognition, polish, and user confidence during comparison workflows.

## What Changes

- Add a webapp brand asset (logo/icon) under webapp resources and render it in the login experience.
- Update login layout to show the logo in a right-side branding area while preserving form usability.
- Add compact branding in the authenticated main shell, including a smaller logo and visible `CFCT` name in the top menu bar.
- Ensure branding elements are deterministic and testable using stable selectors.

## Capabilities

### New Capabilities
- `webapp-branding-assets`: Defines requirements for logo asset usage and placement in login and authenticated shell contexts.

### Modified Capabilities
- `webapp-login-connection-auth`: Extend login modal requirements to include branding placement alongside connection form inputs.
- `webapp-main-ui-layout`: Extend navbar requirements to include compact branding icon and `CFCT` product name.

## Impact

This change affects Vaadin UI composition in `cfct-webapp` login and main view components.
This change adds static image assets under `cfct-webapp/src/main/resources`.
This change affects UI tests that assert navbar and login structure.
No API contract or comparison engine behavior changes are expected.
