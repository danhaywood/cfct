## ADDED Requirements

### Requirement: Automation response includes independent audit-trail comparison
Every successful automation comparison response SHALL include a top-level `auditTrailComparison` object for the selected foreground command.
The object SHALL include an independent `hasDifferences` field and separate foreground and background scope results.
Each scope SHALL include app-a and app-b total audit-record counts and semantic-key count differences.
The existing top-level `hasDifferences` field SHALL continue to represent business-table divergence only.

#### Scenario: Successful response includes clean audit result
- **WHEN** an authenticated automation comparison succeeds
- **AND** foreground and completed-background audit structure agrees
- **THEN** the response includes `auditTrailComparison.hasDifferences` set to `false`
- **AND** the existing business comparison fields retain their current meaning

#### Scenario: Successful response includes audit divergence
- **WHEN** an authenticated automation comparison succeeds
- **AND** foreground or completed-background audit structure differs
- **THEN** the response includes `auditTrailComparison.hasDifferences` set to `true`
- **AND** the differing scope contains the semantic-key count details
- **AND** the top-level `hasDifferences` value is determined only by business-table comparison

#### Scenario: No business tables are resolved
- **WHEN** the selected foreground command resolves no eligible business tables
- **THEN** the successful empty business comparison response still includes the audit-trail comparison for that foreground command and its completed descendants

#### Scenario: Latest-command startup refresh includes audit result
- **WHEN** the automation endpoint refreshes the latest completed foreground command during a startup re-check
- **THEN** the response includes an audit-trail comparison derived from that command and its completed background descendants
