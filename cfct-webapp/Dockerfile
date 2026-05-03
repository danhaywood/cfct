FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace
ARG JAR_FILE=cfct-webapp/target/cfct-webapp-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=builder /workspace/dependencies/ ./
COPY --from=builder /workspace/spring-boot-loader/ ./
COPY --from=builder /workspace/snapshot-dependencies/ ./
COPY --from=builder /workspace/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "org.springframework.boot.loader.launch.JarLauncher"]
