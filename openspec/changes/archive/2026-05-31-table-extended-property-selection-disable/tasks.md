## 1. Metadata Eligibility Detection

- [x] 1.1 Identify the business table catalog query and extend it to read the configured table-level extended property.
- [x] 1.2 Implement truthy evaluation for the table-level property and map matching tables to an ineligible eligibility reason.
- [x] 1.3 Ensure the same eligibility result is used by manual, keyboard, and command-driven table selection paths.

## 2. UI Feedback and Regression Coverage

- [x] 2.1 Update manual table grid row rendering to keep metadata-excluded tables visible but disabled.
- [x] 2.2 Add or update tooltip text for metadata-excluded rows to explain metadata-based exclusion.
- [x] 2.3 Add or update tests for truthy/falsy property handling, disabled row behavior, tooltip output, and command-driven selection guardrails.
