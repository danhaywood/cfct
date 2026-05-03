## Why

The project currently builds a container image but does not automatically publish it to Docker Hub.
Adding a parameterized GitHub Actions workflow enables repeatable, secure image publishing and makes releases easier to consume.

## What Changes

- Add a GitHub Actions workflow that builds and pushes the Docker image to Docker Hub.
- Parameterize image name, tags, and publication triggers through workflow inputs and repository configuration.
- Use GitHub repository secrets for Docker Hub credentials and fail safely when secrets are missing.
- Update README documentation with required secrets, optional variables, and expected workflow behavior.

## Capabilities

### New Capabilities
- `dockerhub-image-publish-workflow`: Publish the project container image to Docker Hub via a parameterized GitHub Actions workflow.

### Modified Capabilities
- `webapp-layered-container-image`: Document and align container publication expectations with Docker Hub publishing automation.

## Impact

This affects `.github/workflows/` by introducing a new CI workflow for Docker Hub publication.
This affects `README.md` by adding setup and credential documentation for publishing.
This relies on Docker Hub account credentials stored as GitHub Actions secrets and standard Docker build tooling in GitHub-hosted runners.
