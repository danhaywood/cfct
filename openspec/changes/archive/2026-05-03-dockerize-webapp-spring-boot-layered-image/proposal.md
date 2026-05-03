## Why

The webapp currently runs only from a local JVM process, which makes deployment and environment parity harder.
Creating a Docker image with Spring Boot layering support enables reproducible deployment artifacts with better build cache reuse and faster image rebuilds.

## What Changes

- Add build and packaging support to produce a container image for the webapp module.
- Configure Spring Boot layered jar metadata so dependencies and application classes are separated into stable layers.
- Add a Dockerfile that uses Spring Boot layertools to assemble a layered runtime image.
- Provide documented commands to build and run the image locally.

## Capabilities

### New Capabilities
- `webapp-layered-container-image`: Build and run the webapp as a Docker image assembled from Spring Boot layers for cache-efficient rebuilds.

### Modified Capabilities
- `vaadin-webapp-configuration`: Add normative packaging expectations for containerized execution of the webapp.

## Impact

Affected code includes the webapp Maven build configuration and new Docker build assets.
The change introduces a container build dependency on a Java runtime base image and Docker-compatible tooling in CI/local environments.
