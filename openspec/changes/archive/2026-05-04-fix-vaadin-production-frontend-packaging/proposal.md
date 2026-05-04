## Why

The Docker swarm deployment starts the Spring Boot process, but browser access fails because Vaadin production assets are missing from the packaged webapp artifact.
This blocks containerized UI usage and violates the expectation that the published image is directly runnable in production mode.

## What Changes

- Update webapp packaging so Maven always produces Vaadin production frontend artifacts required at runtime.
- Ensure the container image is built from an artifact that contains `index.html` and related Vaadin frontend resources.
- Add validation guidance so maintainers can verify the packaged artifact includes required Vaadin production assets before publishing.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `webapp-layered-container-image`: Strengthen runtime packaging requirements so the layered image includes Vaadin production frontend artifacts and serves the UI without missing-classpath failures.

## Impact

This change impacts `cfct-webapp/pom.xml` build configuration, container build workflow assumptions, and deployment validation steps for Docker image publication.
No public API contracts change, but container runtime behavior becomes reliably production-ready for Vaadin UI serving.
