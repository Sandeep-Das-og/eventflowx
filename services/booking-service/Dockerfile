FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=services/booking-service/build/libs/eventflowx-booking-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
