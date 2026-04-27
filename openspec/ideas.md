# Ideas

This file is a lightweight parking lot for ideas that are not yet ready to become OpenSpec changes.
Use it for questions, possible future directions, design sketches, and product thoughts that need more exploration.

When an idea becomes concrete enough to describe as a bounded change, promote it into `openspec/changes/<change-name>/` with a proposal, design, specs, and tasks.
Until then, ideas here are notes only and do not represent current system behavior or committed future work.

## Candidate ideas

- error handling if invalid table specified: for Excel format, for Json format
- Specify multiple tables by way of a file
- Configure ignored columns beyond the current default `id` and `version`.
- Identify ignored columns from extended properties.
- Make it a strategy as to how to ignore columns, with a fallback as per the above rules.
- Render comparison results as JSON for tooling or future webapp use.
- Render comparison results as Excel for tooling or future webapp use.
- Add a CLI command on top of the core comparison library.
- Explore a webapp for running comparisons and browsing reports.
