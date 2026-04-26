## 1. JSON request model

- [x] 1.1 Add a JSON comparison request model with output type and ordered table references.
- [x] 1.2 Add parsing support for loading the request from a JSON file or stream.
- [x] 1.3 Map parsed table references onto the existing multi-table comparison request model.
- [x] 1.4 Validate that the JSON request includes at least one table reference.
- [x] 1.5 Validate that the JSON request includes an output type.

## 2. Output type validation

- [x] 2.1 Accept `json` as the only supported output type.
- [x] 2.2 Reject unsupported output types with a clear validation error.
- [x] 2.3 Add focused tests for missing, empty, and unsupported output types.

## 3. JSON result rendering

- [x] 3.1 Add deterministic JSON rendering for multi-table comparison results.
- [x] 3.2 Include table name, business-key metadata, compared columns, ignored columns, missing rows, and differing rows using stable field names.
- [x] 3.3 Preserve table order and row/difference order in the rendered JSON.
- [x] 3.4 Avoid serializing internal records blindly if that would make the output contract unstable.

## 4. Configured comparison service

- [x] 4.1 Add a library-level service that loads a JSON comparison file, validates the requested output type, runs the comparison, and returns rendered JSON.
- [x] 4.2 Keep the service independent of CLI argument parsing and web request handling.

## 5. Integration and approval tests

- [x] 5.1 Add a JSON test resource that requests comparison for selected tables and output type `json`.
- [x] 5.2 Initialize the required table fixtures in the integration test.
- [x] 5.3 Run the configured comparison from the JSON file.
- [x] 5.4 Update or add the Approval test so the approved output is JSON.
- [x] 5.5 Verify the JSON output contains the expected selected tables and excludes unrequested tables.

## 6. Validation

- [x] 6.1 Run the relevant unit and integration tests.
- [x] 6.2 Run OpenSpec validation for the change.
