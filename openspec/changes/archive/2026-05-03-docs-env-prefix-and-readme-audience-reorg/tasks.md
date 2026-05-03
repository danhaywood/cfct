## 1. Prefix Migration in Docs and Config Templates

- [x] 1.1 Replace `COMPAREDB_` references with `CFCT_` in README wrapper documentation and examples.
- [x] 1.2 Replace `SQLCOMPARER_` and `SQLCOMPARE_` references with `CFCT_` in README, `.env.TEMPLATE`, and `demo/.env` documentation/examples.
- [x] 1.3 Update CLI dotenv-key documentation and parser-related user-facing references to `CFCT_` naming.

## 2. README Audience Reorganization

- [x] 2.1 Restructure README into audience-first sections: Developer and User.
- [x] 2.2 Split User guidance into distinct Webapp and CLI usage flows with clear step-by-step examples.
- [x] 2.3 Preserve and update key setup/run commands while removing stale or conflicting guidance.

## 3. Screenshot Refresh and Curation

- [x] 3.1 Verify README-referenced screenshots are current for the documented workflows.
- [x] 3.2 Refresh or regenerate outdated screenshots used in README user workflows.
- [x] 3.3 Remove branding-logo-only screenshot requirements and references from documentation.

## 4. Verification

- [x] 4.1 Run relevant doc-adjacent tests/checks (including any Playwright screenshot-producing tests impacted by docs changes).
- [x] 4.2 Review README end-to-end for audience clarity, terminology consistency, and command correctness.
