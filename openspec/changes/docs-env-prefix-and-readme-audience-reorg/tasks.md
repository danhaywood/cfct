## 1. Prefix Migration in Docs and Config Templates

- [ ] 1.1 Replace `COMPAREDB_` references with `CFCT_` in README wrapper documentation and examples.
- [ ] 1.2 Replace `SQLCOMPARER_` and `SQLCOMPARE_` references with `CFCT_` in README, `.env.TEMPLATE`, and `demo/.env` documentation/examples.
- [ ] 1.3 Update CLI dotenv-key documentation and parser-related user-facing references to `CFCT_` naming.

## 2. README Audience Reorganization

- [ ] 2.1 Restructure README into audience-first sections: Developer and User.
- [ ] 2.2 Split User guidance into distinct Webapp and CLI usage flows with clear step-by-step examples.
- [ ] 2.3 Preserve and update key setup/run commands while removing stale or conflicting guidance.

## 3. Screenshot Refresh and Curation

- [ ] 3.1 Verify README-referenced screenshots are current for the documented workflows.
- [ ] 3.2 Refresh or regenerate outdated screenshots used in README user workflows.
- [ ] 3.3 Remove branding-logo-only screenshot requirements and references from documentation.

## 4. Verification

- [ ] 4.1 Run relevant doc-adjacent tests/checks (including any Playwright screenshot-producing tests impacted by docs changes).
- [ ] 4.2 Review README end-to-end for audience clarity, terminology consistency, and command correctness.
