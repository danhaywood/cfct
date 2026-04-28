# Ideas

This file is a lightweight parking lot for ideas that are not yet ready to become OpenSpec changes.
Use it for questions, possible future directions, design sketches, and product thoughts that need more exploration.

When an idea becomes concrete enough to describe as a bounded change, promote it into `openspec/changes/<change-name>/` with a proposal, design, specs, and tasks.
Until then, ideas here are notes only and do not represent current system behavior or committed future work.

## Candidate ideas

### Cosmetic

- webapp:
  - status line still ugly
  - download buttons , show above filter, right align.
  - if the grid is too wide or too high for area, add appropriate scrollbars.
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
- have the cli/webapp support Azure EntraID credentials
- auto select from command/audit
  - drop down from/to of commands
  - query the audits
  - from audit targets infer entities
  - map entities to tables
- use spring native to compile the cli into an exe.  Put this behind a profile, only built on demand (I believe it can take some time to run).  Update README to explain how to build, using this profile.  Update README to explain how to run (instead of using that shell script).
- create a docker image out of the webapp.

### Programming Style

- don't hardcode package names, eg '@SpringBootApplication(scanBasePackages = "com.danhaywood.sqlcomparer.cli")'
- clean up HomePageConnectionStatusPlaywrightSuccessTest
  - externalize DDL
  - PlaywrightSqlServerFixture should be a service, not static methods
- playwright, use page objects
- use lombok to remove getter/setter boilerplate
