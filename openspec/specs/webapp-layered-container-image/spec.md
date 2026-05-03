# webapp-layered-container-image Specification

## Purpose
TBD - created by archiving change dockerize-webapp-spring-boot-layered-image. Update Purpose after archive.
## Requirements
### Requirement: Webapp module builds a Spring Boot layered container image
The system SHALL provide a Dockerfile for `cfct-webapp` that produces a runnable container image.
The image build SHALL use Spring Boot layered-jar metadata and layertools extraction to separate dependencies, loader, snapshots, and application classes into distinct image layers.
The runtime image SHALL launch the webapp with an entrypoint equivalent to Spring Boot `JarLauncher` behavior.

#### Scenario: Docker build produces runnable image
- **WHEN** a developer builds the webapp container image from the repository
- **THEN** Docker completes successfully and produces an image that starts the Spring Boot webapp process

#### Scenario: Layered extraction isolates stable and volatile content
- **WHEN** the image build performs layertools extraction on the packaged webapp jar
- **THEN** dependency-related artifacts and application classes are copied in separate Docker layers

### Requirement: Layered image workflow is documented for contributors
The system SHALL document commands to build and run the layered webapp image locally.
The documentation SHALL identify required prerequisites for container builds.
The documentation SHALL describe automated Docker Hub publication via GitHub Actions.
The documentation SHALL list required GitHub repository secrets for Docker Hub authentication and minimal credential scope expectations.
The documentation SHALL explain how to pull and run the published Docker Hub image.
The documentation SHALL explain how to mount an external `application.yml` into the container to override default configuration.

#### Scenario: Contributor follows documented container workflow
- **WHEN** a contributor follows the documented build and run commands
- **THEN** they can build the image and access the running webapp without undocumented steps

#### Scenario: Maintainer configures publish credentials from documentation
- **WHEN** a maintainer follows the documented GitHub Actions setup for Docker Hub publishing
- **THEN** they can provide the required secrets and run the publish workflow without additional undocumented configuration

#### Scenario: Operator runs published image with mounted config
- **WHEN** an operator follows the documented Docker Hub consumption guidance and mounts an external `application.yml`
- **THEN** the container starts using the mounted configuration overrides without requiring image rebuilds

