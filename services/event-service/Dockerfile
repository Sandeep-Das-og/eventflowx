FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=services/event-service/build/libs/eventflowx-event-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
