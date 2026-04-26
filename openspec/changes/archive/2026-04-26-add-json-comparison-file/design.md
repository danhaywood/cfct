## Context

The library is moving from hard-coded test requests toward reusable comparison inputs.
The multi-table comparison API provides the natural target model for a file-based request, but callers should not need to construct Java objects directly in every scenario.

This change introduces JSON as the first external comparison configuration format.
It also makes JSON the first supported external output format for configured comparisons.

## Goals / Non-Goals

**Goals:**

- Load a comparison request from a JSON file or stream.
- Represent table references in JSON using schema and table names.
- Require an output type in the JSON file.
- Support only `json` as the output type for this change.
- Render comparison results as deterministic JSON.
- Update approval coverage so configured comparison output is approved as JSON.

**Non-Goals:**

- Do not add a CLI command.
- Do not add a web endpoint.
- Do not support YAML or other input formats.
- Do not support text, HTML, XML, or other output types from the JSON file.
- Do not auto-discover tables.
- Do not change the existing comparison semantics for table matching or row differences.

## Decisions

### Use a small JSON request shape

The JSON file should describe the output type and requested tables.
A representative shape is:

```json
{
  "output": {
    "type": "json"
  },
  "tables": [
    { "schema": "dbo", "name": "Supplier" },
    { "schema": "dbo", "name": "Product" }
  ]
}
```

This keeps the input aligned with the existing explicit table-selection API.
It also leaves room for later options without requiring CLI-specific concepts.

Alternative considered: make the JSON file mirror internal Java record names exactly.
This was rejected because the file should be a stable user-facing contract rather than a serialization leak of implementation types.

### Require output type and support only JSON now

The request file should include an output type so future CLI or web adapters can use the same contract.
For this change, the only accepted value is `json`.
Unsupported values should fail clearly.

Alternative considered: default the output type when omitted.
This was rejected because an explicit output type makes the file easier to validate and evolve.

### Render deterministic JSON from structured results

The JSON renderer should consume structured comparison results rather than re-querying data.
It should preserve table order, row order, compared columns, ignored columns, and difference ordering.
The JSON should be deterministic so Approval tests are stable.

Alternative considered: approve the parsed Java object directly.
This was rejected because the output contract matters to future CLI and web consumers.

### Keep the change at library level

The implementation should expose services that future CLI and web layers can call.
It should not introduce command-line parsing or web request handling.

## Risks / Trade-offs

- JSON shape may need to evolve once CLI requirements are clearer → Keep the shape small and versionless for now, and add fields later only when needed.
- Output JSON may expose too much internal result structure → Design the renderer output deliberately rather than serializing internal records blindly.
- Existing text approval output may still be useful → Keep text rendering available, but use JSON approval for the configured comparison scenario.
- Unsupported output types could be confused with missing renderer bugs → Validate output type before running the comparison.
