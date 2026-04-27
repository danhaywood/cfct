## Why

The current webapp configuration shape implies that web table selection must mirror CLI table input options.
We need to decouple web selection behavior now so the UI can evolve toward automated and manual workflows without inheriting CLI constraints.

## What Changes

- Introduce a webapp-side `SelectionPlan` abstraction that resolves comparison targets as `List<TableRef>`.
- Define webapp behavior so table-selection strategy is independent from CLI flags such as `-t` and `--tables-file`.
- Keep shared execution configuration alignment for connection, environment file, and output settings.
- Add an initial explicit selection plan implementation that stores concrete `TableRef` values for immediate use.
- Update docs and tests to reflect the new boundary between shared execution config and channel-specific selection strategy.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `vaadin-webapp-configuration`: Refine requirements so webapp table selection is strategy-driven through `SelectionPlan` instead of CLI-style table-input parity.

## Impact

This change affects webapp configuration contracts, execution wiring, and documentation in the `sqlcomparer-webapp` module.
This change does not alter the CLI parser contract or the comparison engine interfaces beyond introducing selection strategy composition in the webapp path.
