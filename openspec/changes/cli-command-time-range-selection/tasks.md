## 1. CLI Argument Model and Validation

- [ ] 1.1 Add CLI options for command time-range start and end.
- [ ] 1.2 Enforce exactly one table-selection mode: explicit table input or command-time-range input.
- [ ] 1.3 Validate time-range arguments, including required pair semantics and timestamp parsing errors.

## 2. Command Selection and Table Inference Flow

- [ ] 2.1 Resolve selected commands from the provided time range using inclusive boundaries.
- [ ] 2.2 Infer business tables from the selected commands using existing command-footprint resolution.
- [ ] 2.3 Fail with clear errors when no commands are selected or no business tables are inferred.
- [ ] 2.4 Build comparison requests from inferred tables while preserving existing explicit-table execution behavior.

## 3. Automated Tests

- [ ] 3.1 Add or update unit tests for parser/validation behavior for table-mode exclusivity and time-range argument handling.
- [ ] 3.2 Add or update tests for inclusive start/end command selection behavior.
- [ ] 3.3 Add or update tests for inferred table request construction and empty-result failures.

## 4. Documentation and Verification

- [ ] 4.1 Update README with time-range CLI usage, argument semantics, and examples.
- [ ] 4.2 Run relevant CLI module tests covering argument parsing and command-driven table inference.
