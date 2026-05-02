## ADDED Requirements

### Requirement: Canonical project naming is cfct
The project SHALL use `cfct` as the canonical user-facing short name.
Documentation SHALL treat `cfct` as `Command Footprint Comparison Tool`.

#### Scenario: Docs use cfct naming
- **WHEN** users read project docs and usage snippets
- **THEN** user-facing naming uses `cfct`
- **AND** long-form expansion is `Command Footprint Comparison Tool` in relevant introduction contexts

### Requirement: Launcher script is renamed to cfct.sh
The repository SHALL provide `cfct.sh` as the command launcher script.
References to `comparedb.sh` in maintained docs and scripts SHALL be updated to `cfct.sh`.
The renamed launcher SHALL preserve existing behavior and arguments.

#### Scenario: Renamed launcher path is documented and usable
- **WHEN** users follow project script usage documentation
- **THEN** they invoke `cfct.sh`
- **AND** command behavior matches prior `comparedb.sh` behavior
