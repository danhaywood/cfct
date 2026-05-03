## MODIFIED Requirements

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
