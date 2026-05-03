## Context

The repository contains a Spring Boot and Vaadin webapp module named `cfct-webapp` that is packaged as a jar for local execution.
There is currently no committed container build flow, so deployment and runtime parity rely on ad hoc local setup.
Spring Boot supports layered jars and layertools, which allow Docker images to be assembled from stable dependency layers and frequently-changing application layers.

## Goals / Non-Goals

**Goals:**
- Produce a repeatable Docker image build for the webapp module.
- Use Spring Boot layering so Docker cache reuse minimizes rebuild time when only application classes change.
- Keep runtime image lean and deterministic by extracting jar layers into explicit filesystem layers.
- Document local build and run commands for contributors and CI automation.

**Non-Goals:**
- Introduce Kubernetes manifests or production orchestration assets.
- Redesign webapp runtime behavior, authentication flow, or UI functionality.
- Add non-Docker packaging formats.

## Decisions

Use Spring Boot Maven plugin layered-jar support in `cfct-webapp` packaging configuration.
This keeps layer metadata aligned with the produced jar and avoids custom jar-manipulation scripts.

Use a multi-stage Dockerfile.
The builder stage extracts layers using `java -Djarmode=layertools -jar app.jar extract`, and the runtime stage copies extracted layers in dependency-first order.
This maximizes Docker cache hits for dependency layers.

Use a JRE runtime base image compatible with the project Java version.
This keeps the runtime image smaller than a full JDK while preserving compatibility.

Expose a single container entrypoint that launches `org.springframework.boot.loader.JarLauncher` from the extracted layers.
This keeps runtime behavior equivalent to `java -jar` execution.

Provide default port exposure aligned with the webapp server port.
This supports local docker run usage without extra discovery steps.

## Risks / Trade-offs

[Risk: Base image drift introduces security or compatibility issues] → Mitigation: Pin image tags to explicit versions and update through dependency maintenance.
[Risk: Layer extraction behavior changes with Spring Boot upgrades] → Mitigation: Keep plugin and runtime versions aligned and validate image build in CI.
[Risk: Container startup environment differs from local JVM expectations] → Mitigation: Document required environment variables and run smoke tests against containerized startup.
[Trade-off: Additional Dockerfile and plugin configuration complexity] → Benefit: Faster incremental rebuilds and deployment portability.

## Migration Plan

Add layering and image build configuration in the webapp module without removing existing jar packaging flow.
Introduce Dockerfile and documentation in the repository so both local and CI workflows can adopt the new image.
Validate by building the image and starting the container to confirm webapp reachability.
If issues occur, revert Docker-specific configuration while preserving existing non-container execution paths.

## Open Questions

Should the image be built directly via Dockerfile only, or also via Spring Boot build-image goals for CI parity.
Which exact runtime base image family should be standard for the project’s security and size expectations.
