## 1. Webapp Build Layering Configuration

- [ ] 1.1 Update `cfct-webapp/pom.xml` to enable Spring Boot layered jar metadata in packaging output.
- [ ] 1.2 Verify the packaged webapp jar supports layertools extraction in local build output.

## 2. Docker Image Assembly

- [ ] 2.1 Add a multi-stage Dockerfile for `cfct-webapp` that extracts and copies Spring Boot layers in cache-friendly order.
- [ ] 2.2 Configure container entrypoint and exposed port to start the webapp in runtime stage.
- [ ] 2.3 Build the Docker image locally and confirm container startup succeeds.

## 3. Documentation and Validation

- [ ] 3.1 Document prerequisites plus build and run commands for the layered webapp image.
- [ ] 3.2 Add or update verification steps so CI or local checks confirm layered image build remains healthy.
