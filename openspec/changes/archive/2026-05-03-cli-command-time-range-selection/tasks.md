## 1. CLI Argument Model and Validation

- [x] 1.1 Add CLI options for command time-range start and end.
- [x] 1.2 Enforce exactly one table-selection mode: explicit table input or command-time-range input.
- [x] 1.3 Validate time-range arguments, including required pair semantics and timestamp parsing errors.

## 2. Command Selection and Table Inference Flow

- [x] 2.1 Resolve selected commands from the provided time range using inclusive boundaries.
- [x] 2.2 Infer business tables from the selected commands using existing command-footprint resolution.
- [x] 2.3 Fail with clear errors when no commands are selected or no business tables are inferred.
- [x] 2.4 Build comparison requests from inferred tables while preserving existing explicit-table execution behavior.

## 3. Automated Tests

- [x] 3.1 Add or update unit tests for parser/validation behavior for table-mode exclusivity and time-range argument handling.
- [x] 3.2 Add or update tests for inclusive start/end command selection behavior.
- [x] 3.3 Add or update tests for inferred table request construction and empty-result failures.

## 4. Documentation and Verification

- [x] 4.1 Update README with time-range CLI usage, argument semantics, and examples.
- [x] 4.2 Run relevant CLI module tests covering argument parsing and command-driven table inference.
