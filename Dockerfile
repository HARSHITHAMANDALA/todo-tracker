# ---------- Stage 1: build the JAR ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- Stage 2: small runtime image ----------
FROM eclipse-temurin:17-jre-alpine
ARG APP_VERSION=dev
ENV APP_VERSION=${APP_VERSION}

# Run as a non-root user (numeric UID so Kubernetes can verify it)
RUN addgroup -g 1001 -S app && adduser -u 1001 -S app -G app
WORKDIR /app
COPY --from=build /workspace/target/todo-tracker-*.jar app.jar
USER 1001

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]