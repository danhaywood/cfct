## MODIFIED Requirements

### Requirement: Webapp module builds a Spring Boot layered container image
The system SHALL provide a Dockerfile for `cfct-webapp` that produces a runnable container image.
The image build SHALL use Spring Boot layered-jar metadata and layertools extraction to separate dependencies, loader, snapshots, and application classes into distinct image layers.
The runtime image SHALL launch the webapp with an entrypoint equivalent to Spring Boot `JarLauncher` behavior.
The packaged webapp artifact used for image assembly SHALL include Vaadin production frontend resources required at runtime, including `index.html` on the classpath.
The containerized runtime SHALL serve the Vaadin UI in production mode without `Unable to find index.html` classpath errors.

#### Scenario: Docker build produces runnable image
- **WHEN** a developer builds the webapp container image from the repository
- **THEN** Docker completes successfully and produces an image that starts the Spring Boot webapp process

#### Scenario: Layered extraction isolates stable and volatile content
- **WHEN** the image build performs layertools extraction on the packaged webapp jar
- **THEN** dependency-related artifacts and application classes are copied in separate Docker layers

#### Scenario: Container image includes Vaadin production frontend assets
- **WHEN** the packaged webapp jar used by the Dockerfile is inspected
- **THEN** classpath resources include Vaadin production frontend artifacts, including `index.html`

#### Scenario: Browser request succeeds in production mode
- **WHEN** an operator runs the container image with production mode enabled and accesses the root URL
- **THEN** the application returns the Vaadin UI shell rather than an error caused by missing `index.html`
