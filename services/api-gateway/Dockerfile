FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=services/api-gateway/build/libs/eventflowx-api-gateway-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
