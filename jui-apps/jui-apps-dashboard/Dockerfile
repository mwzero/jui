#FROM eclipse-temurin:21-jdk-alpine
FROM openjdk:17-jdk-slim
ARG JAR_FILE=JAR_FILE_MUST_BE_SPECIFIED_AS_BUILD_ARG
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]