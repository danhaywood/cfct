## Why

The core comparison library currently hard-codes technical-column ignore decisions.
That makes extension and environment-specific policy control difficult.

## What Changes

- Add an `IgnoreColumnAdvisor` SPI in `cfct-api` for deciding whether a column should be ignored.
- Update core comparison logic to consult an injected `List<IgnoreColumnAdvisor>` so multiple advisors can contribute decisions.
- Replace current built-in ignore logic with three separate advisor beans:
  - identity-column advisor,
  - uuid/guid-column advisor,
  - timestamp/version-column advisor.
- Add typed Spring `@ConfigurationProperties` for each advisor so each can be enabled or disabled independently.
- Default all advisor enablement flags to enabled.

## Capabilities

### New Capabilities
- `ignore-column-advisor-spi`: Public SPI and advisor-composition behavior for column-ignore decisions in comparison.

### Modified Capabilities
- `core-single-table-comparison`: Replace fixed built-in ignored-column rules with composed advisor-driven rules while preserving default behavior.
- `vaadin-webapp-configuration`: Add typed configuration properties for enabling or disabling each default ignore-column advisor.

## Impact

- Affected modules: `cfct-api`, `cfct-impl`, and `cfct-webapp` configuration wiring.
- Affected tests for ignored-column behavior and configuration binding.
- No breaking CLI argument changes are expected.
