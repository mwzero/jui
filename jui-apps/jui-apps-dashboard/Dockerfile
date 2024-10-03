#FROM eclipse-temurin:21-jdk-alpine
FROM openjdk:17-jdk-slim

ADD https://github-registry-files.githubusercontent.com/847271373/83575b00-81a2-11ef-9714-a44cd7440d6c?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20241003%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20241003T164925Z&X-Amz-Expires=300&X-Amz-Signature=5af9a65ff5c83d25721532f0ac767a2cbd8399c3ccf124a1f2d022ee178fc70a&X-Amz-SignedHeaders=host&response-content-disposition=filename%3Djui-apps-dashboard-0.0.1-20241003.161403-1.jar&response-content-type=application%2Foctet-stream /tmp/app.jar
#COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]