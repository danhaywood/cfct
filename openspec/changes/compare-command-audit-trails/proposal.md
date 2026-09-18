## Why

CFCT currently uses command audit records to discover the business-table footprint but does not verify that the two applications produced equivalent audit records.
A replay can therefore pass when its business data agrees but audit generation is missing or structurally different on one side.

## What Changes

- Add a dedicated audit-trail comparison for the selected foreground command and its completed background descendants.
- Compare audit structure by occurrence counts grouped on the semantic key of target and audited member identifier (`propertyId` in the canonical `AuditTrailEntry` fixture), rather than generated row identity, timestamp, transaction sequence, or background child interaction identifier.
- Query foreground audit records using the shared foreground interaction identifier and query background records using each side's independently discovered completed descendant identifiers.
- Return foreground and background audit comparison results separately from the existing business-table comparison result.
- Preserve the existing meaning of top-level `hasDifferences`; audit divergence is reported through its own status so clients can choose policy.
- Apply the audit comparison during normal automation refresh and startup re-checks of the latest completed foreground command.
- Keep post-value comparison and post-value masking or fuzzy normalization out of this first increment while leaving the result model extensible for them.

## Capabilities

### New Capabilities

- `command-audit-trail-comparison`: Resolves, groups, and compares foreground and background `AuditTrailEntry` records by semantic-key occurrence counts.

### Modified Capabilities

- `webapp-automation-rest-api`: Includes a separate audit-trail comparison in each successful automation comparison response, including empty business-footprint responses.

## Impact

The change affects CFCT's SQL Server command/audit data access, automation comparison orchestration, JSON response model and formatting, fixture schemas and data, integration and web tests, and user documentation.
The canonical command and audit tables or compatibility views must expose the interaction, parent interaction, target, and logical member identifier columns needed by the comparison.
Existing clients remain compatible because top-level business comparison fields retain their current semantics and the audit result is additive.
