## Context

The current layered container image launches with `-Dvaadin.productionMode=true`.
The packaged webapp jar does not currently include Vaadin production frontend artifacts such as `index.html`.
At runtime, Vaadin fails request handling with `Unable to find index.html` and the app returns a whitelabel error path.
The fix must preserve layered-jar container behavior while ensuring artifact completeness for production mode.

## Goals / Non-Goals

**Goals:**

Guarantee that the build used for container images includes Vaadin production frontend outputs on the classpath.
Keep the layered Docker image approach and Spring Boot launcher behavior unchanged.
Provide a deterministic validation step so release workflows can detect missing frontend artifacts before deployment.

**Non-Goals:**

Changing application UI behavior or adding new routes.
Redesigning Docker entrypoint strategy beyond what is required for asset availability.
Introducing alternate runtime modes that depend on development-time Vaadin frontend generation.

## Decisions

Use Maven-side Vaadin production frontend build integration as the source of truth for artifact completeness.
This keeps runtime startup simple and avoids generating frontend assets inside containers at boot time.
Prefer build-time failure when production assets are missing over runtime failure after deployment.
Retain `-Dvaadin.productionMode=true` in runtime configuration because production mode is the intended deployment mode.
Define artifact verification as part of packaging workflow so CI and local builds can assert `index.html` presence in the jar.

## Risks / Trade-offs

[Longer Maven packaging time] → Mitigate by accepting one-time frontend build cost during artifact creation instead of runtime failure risk.
[Profile or plugin misconfiguration across environments] → Mitigate by documenting the exact production packaging path used for container builds.
[False confidence if verification is skipped] → Mitigate by adding explicit verification to release workflow expectations and contributor docs.
