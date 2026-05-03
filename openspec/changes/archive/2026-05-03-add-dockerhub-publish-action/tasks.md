## 1. GitHub Actions Docker Hub publish workflow

- [x] 1.1 Create `.github/workflows/dockerhub-publish.yml` with publish triggers for default branch, release tags, and manual dispatch.
- [x] 1.2 Configure Docker Hub authentication using `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` secrets with explicit missing-secret failure behavior.
- [x] 1.3 Add metadata-driven tagging and build/push steps using Docker GitHub Actions so published tags include deterministic branch/release and commit-SHA forms.

## 2. Repository parameterization and safeguards

- [x] 2.1 Parameterize image repository/name and optional tag overrides through workflow inputs or environment variables with sensible defaults.
- [x] 2.2 Add guard conditions so image push only occurs on approved publish events and trusted refs.
- [x] 2.3 Validate workflow syntax and execution path with at least one dry-run or manual-dispatch verification.

## 3. Documentation updates

- [x] 3.1 Update `README.md` with a Docker Hub publishing section describing workflow purpose and trigger behavior.
- [x] 3.2 Document required GitHub secrets, how to create Docker Hub access tokens, and minimal required token scope.
- [x] 3.3 Document optional workflow parameters (image repository and tagging behavior) and provide a short maintainer setup checklist.
