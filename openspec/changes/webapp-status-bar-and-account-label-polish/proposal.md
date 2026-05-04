## Why

The bottom status bar currently competes for vertical space with the left-drawer compare action, creating overlap risk and reducing usability on smaller viewports.
Users also need clearer identity and cleaner footer content, including visible account username context and less noisy connection details.

## What Changes

- Add spacing/layout safeguards so the fixed bottom status bar no longer overlaps the Compare action area.
- Simplify status-bar connection details by removing the displayed JDBC server URL and showing only left/right database names.
- Update the top-right Account menu label to include the authenticated username value.
- Improve horizontal spacing in the status bar so labels and values have consistent readable padding.

## Capabilities

### New Capabilities
- `<name>`: None.

### Modified Capabilities
- `webapp-main-ui-layout`: Adjust footer/status bar spacing, displayed connection details, and compare-action clearance behavior.
- `webapp-login-connection-auth`: Surface authenticated username in the account menu label.

## Impact

Changes affect Vaadin main layout composition, footer rendering, account-menu rendering, and associated UI tests.
No backend API or dependency changes are expected.