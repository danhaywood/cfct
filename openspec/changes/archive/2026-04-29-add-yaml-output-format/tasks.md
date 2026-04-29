## 1. Output Format Model and Validation

- [x] 1.1 Add `yaml` to the shared output format enum or discriminator used by library and CLI paths.
- [x] 1.2 Update output format parsing and validation so CLI and comparison-file inputs accept `yaml` and reject unsupported values with clear errors.

## 2. YAML Serialization in Library

- [x] 2.1 Implement YAML serialization for comparison reports using the same logical structure and ordering guarantees as JSON output.
- [x] 2.2 Add or configure YAML serialization dependency support behind the existing output writer abstraction.
- [x] 2.3 Add tests that verify deterministic YAML output structure, ordering, and row-level detail for representative fixtures.

## 3. CLI Output Handling

- [x] 3.1 Extend CLI renderer dispatch to route `--output-format yaml` to the YAML writer.
- [x] 3.2 Ensure CLI output destination behavior supports YAML to stdout by default and to file when `-o` is provided.
- [x] 3.3 Add CLI tests for YAML format selection, stdout behavior, file output behavior, and unsupported format failures.

## 4. Comparison File Output-Type Support

- [x] 4.1 Extend comparison JSON-file output type handling to accept `yaml` alongside existing formats.
- [x] 4.2 Add integration coverage for comparison-file-driven YAML output generation and validation errors.

## 5. Documentation and Examples

- [x] 5.1 Update CLI usage docs and examples to include YAML output commands and expected file extensions.
- [x] 5.2 Update any output-format capability docs to reflect support for `text`, `json`, `yaml`, and `excel` where applicable.
