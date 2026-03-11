# Lightweight Dockerfile to run tests inside a Maven + JDK container.
# The repository is mounted at /workspace in docker-compose.yml; this Dockerfile is optional
# because docker-compose uses the official maven image directly. Kept for users who want a local image.

FROM maven:3.9.4-eclipse-temurin-17
WORKDIR /workspace
COPY . /workspace
# Use entrypoint in docker-compose to run tests
