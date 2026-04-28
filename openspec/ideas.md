# Ideas

This file is a lightweight parking lot for ideas that are not yet ready to become OpenSpec changes.
Use it for questions, possible future directions, design sketches, and product thoughts that need more exploration.

When an idea becomes concrete enough to describe as a bounded change, promote it into `openspec/changes/<change-name>/` with a proposal, design, specs, and tasks.
Until then, ideas here are notes only and do not represent current system behavior or committed future work.

## Candidate ideas

### Cosmetic

- webapp:
  - colour the cells in the webapp
  - status line still ugly
  - download buttons , show above filter, right align.
  - wide grid, add scroll
  - theming
  - start with focus on the selection table, on 'select' column.
  - space should toggle selection.  up/down + l/r arrows should behave appropriately 
- excel spreadsheet
  - for detail sheets, don't autosize the 2nd column (should be same width as the 3rd column)

### New features

- Render comparison results as YAML, in the cli and download
- ignored columns
  - Identify ignored columns from extended properties.
  - Make it a strategy as to how to ignore columns, with a fallback as per the above rules.
- generalize the way to find a _PK, care only about the suffix.  So, PurchaseOrder__reference__PK would also be ok.
- have the cli/webapp support Azure EntraID credentials
- auto select from command/audit
  - drop down from/to of commands
  - query the audits
  - from audit targets infer entities
  - map entities to tables

### Programming Style

- don't hardcode package names, eg '@SpringBootApplication(scanBasePackages = "com.danhaywood.sqlcomparer.cli")'
- clean up HomePageConnectionStatusPlaywrightSuccessTest
  - externalize DDL
  - PlaywrightSqlServerFixture should be a service, not static methods
- playwright, use page objects
- use lombok to remove getter/setter boilerplate
