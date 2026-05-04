## 1. Maven packaging for Vaadin production assets

- [x] 1.1 Add or update `cfct-webapp` Maven build configuration so Vaadin production frontend artifacts are generated during container-targeted packaging.
- [x] 1.2 Ensure the packaging path used by Docker image creation consumes that production-built artifact rather than a dev-mode artifact.

## 2. Container workflow validation

- [x] 2.1 Add a repeatable verification step to confirm the packaged jar contains `index.html` and required Vaadin frontend resources before image publication.
- [x] 2.2 Validate container startup and browser access in production mode to confirm absence of `Unable to find index.html` runtime errors.

## 3. Documentation updates

- [x] 3.1 Update container build/run documentation to describe the required production packaging flow for Vaadin.
- [x] 3.2 Document troubleshooting guidance for missing Vaadin frontend assets in containerized deployments.
