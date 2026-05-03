## Context

The repository currently uses mixed prefixes (`COMPAREDB_`, `SQLCOMPARER_`, and possible `SQLCOMPARE_`) in README examples and env templates.
The README also interleaves developer and end-user concerns, making it harder to follow a simple path for either audience.
Current screenshots include branding-oriented images that are no longer required.

## Goals / Non-Goals

**Goals:**
- Standardize documented environment variable prefixes to `CFCT_`.
- Reorganize README into audience-first structure: developer vs user.
- Within user documentation, provide clear webapp-first and CLI-first usage paths.
- Ensure all referenced screenshots are current and relevant.
- Drop branding-logo screenshot requirement.

**Non-Goals:**
- Changing core comparison behavior.
- Redesigning UI components solely for documentation layout.
- Introducing additional product naming aliases beyond the new `CFCT_` standard.

## Decisions

- Adopt `CFCT_` as the single documentation prefix for wrapper and dotenv configuration.
Legacy prefix mentions are removed from primary README paths.
- Reframe README with top-level audience sections.
Developer content covers build/test/fixtures and contribution concerns.
User content covers getting started, then split by webapp and CLI workflows.
- Keep screenshot set minimal and task-oriented.
Include only screenshots that support user workflows and remove branding/logo-only screenshot usage.
- Align docs, examples, and templates in one pass.
This avoids drift where README shows one prefix but templates/scripts show another.

## Risks / Trade-offs

- [Users with older local env files] → Add a short migration note from old prefixes to `CFCT_` names.
- [README restructuring breaks deep links] → Preserve or add clear section anchors and update internal references.
- [Screenshot refresh churn] → Refresh only screenshots referenced by README user workflows.
