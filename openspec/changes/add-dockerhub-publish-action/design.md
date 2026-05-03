## Context

The project already provides a Dockerfile and local documentation for building a layered webapp image.
There is no automated release path that publishes built images to Docker Hub from GitHub Actions.
Publishing requires handling credentials securely, generating deterministic tags, and documenting required repository configuration.

## Goals / Non-Goals

**Goals:**
- Add a GitHub Actions workflow that builds and pushes the webapp image to Docker Hub.
- Keep credentials out of source control by requiring GitHub secrets.
- Parameterize image coordinates and tagging behavior so repositories or forks can adapt without rewriting workflow logic.
- Document setup steps and required credentials in README.

**Non-Goals:**
- Replace the existing Dockerfile or local image build flow.
- Introduce a new container registry beyond Docker Hub.
- Change application runtime behavior inside the container image.

## Decisions

The workflow will live at `.github/workflows/dockerhub-publish.yml` to separate publish concerns from test workflows.
The workflow will use `docker/login-action` for authenticated registry access because it is the standard maintained action for Docker Hub authentication.
The workflow will use `docker/metadata-action` to compute tags from branch, SHA, semver tags, and optional manual input to keep tagging consistent and auditable.
The workflow will use `docker/build-push-action` with `push: true` only when credentials are available and the event is an approved publish trigger.
The image repository name will be configurable via environment or workflow input, with a default based on repository metadata.
Credentials will be provided through `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` repository secrets, where the token is a Docker Hub access token with push rights.
README will include a dedicated section for required secrets, recommended token scope, and expected triggers.

## Risks / Trade-offs

Credential misconfiguration can cause failed publish runs. → Validate secret presence early and provide clear failure messages.
Tag strategy can produce too many tags if not constrained. → Limit generated tags to meaningful branch, release, and SHA forms.
Publishing on every commit can increase registry churn. → Scope publish triggers to default branch, version tags, and optional manual dispatch.

## Migration Plan

Add the workflow file and run it first with `workflow_dispatch` in a controlled branch.
Configure Docker Hub secrets in repository settings before enabling default branch publishing.
Validate pushed images and tags in Docker Hub, then keep publish triggers enabled for normal CI usage.
If rollback is needed, disable the workflow or remove publish triggers while preserving local container build documentation.

## Open Questions

Should publish scope include pull request builds to an ephemeral tag namespace or remain limited to trusted branches only?
Should multi-architecture builds be included now or deferred to a follow-up change?
