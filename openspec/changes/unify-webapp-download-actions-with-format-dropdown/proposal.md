## Why

The comparison results area currently shows separate download buttons, which increases visual clutter and slows the download decision flow.
A single download control with format selection, including YAML support and JSON as the default, gives a cleaner and faster user experience.

## What Changes

- Add webapp download support for YAML format in addition to existing JSON and Excel outputs.
- Replace multiple result download buttons with one consolidated download action and a format-choice dropdown.
- Make JSON the default selected format for the consolidated download control.
- Preserve deterministic selectors and behavior for automated tests.

## Capabilities

### New Capabilities
- `webapp-download-format-selector`: Defines consolidated webapp download control behavior with selectable output formats and default selection.

### Modified Capabilities
- `webapp-comparison-results-tabs`: Extend results-stage action requirements to use one download action with format dropdown and default JSON selection.
- `yaml-comparison-output`: Extend output usage requirements to include webapp download path for YAML exports.

## Impact

This change affects Vaadin results-stage action UI composition in `cfct-webapp`.
This change affects result export wiring in the webapp comparison execution and download link generation.
This change affects webapp tests that currently expect separate download controls.
No core comparison logic changes are expected.
